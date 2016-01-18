package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.body.SubscriptionGroupWrapper;
import com.alibaba.rocketmq.common.subscription.SubscriptionGroupConfig;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.google.common.base.Preconditions;
import com.ndpmedia.rocketmq.cockpit.model.*;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.WarningMapper;
import com.ndpmedia.rocketmq.cockpit.service.*;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitConsumerGroupMQServiceImpl;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import com.ndpmedia.rocketmq.cockpit.util.WarnMsgHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by robert on 2015/6/9.
 * check consumer group
 * 1.auto download consumer group from cluster or broker to database
 * 2. auto check consumer group, create or update consumer group config on cluster or broker.
 */
@Component
public class ConsumerGroupScheduler {

    private Logger logger = LoggerFactory.getLogger(ConsumerGroupScheduler.class);

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private CockpitBrokerMQService cockpitBrokerMQService;

    @Autowired
    private WarningMapper warningMapper;

    /**
     * update consumer group to cluster
     * period:ten minutes
     */
    @Scheduled(fixedRate = 600000)
    public void synchronizeConsumerGroups() {
        logger.info("[MONITOR][CONSUMER-GROUP-SCHEDULER]  schedule start");
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();
            Set<String> brokerAddresses = cockpitBrokerMQService.getALLBrokers(defaultMQAdminExt);
            Set<Long> groupIds = new HashSet<>();
            for (String brokerAddress : brokerAddresses) {
                syncDownConsumerGroupsByBroker(defaultMQAdminExt, brokerAddress);
                syncUpConsumerGroupsByBroker(defaultMQAdminExt, brokerAddress, groupIds);
            }

            for (long groupId:groupIds){
                cockpitConsumerGroupDBService.activate(groupId);
            }

        } catch (Exception e) {
            logger.error("[MONITOR][CONSUMER-GROUP-SCHEDULER]Failed to synchronizeConsumerGroups", e);
        } finally {
            if (null != defaultMQAdminExt)
                defaultMQAdminExt.shutdown();
        }

        logger.info("[MONITOR][CONSUMER-GROUP-SCHEDULER]  schedule end");
    }

    private void syncDownConsumerGroupsByBroker(DefaultMQAdminExt defaultMQAdminExt, String brokerAddress)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Preconditions.checkNotNull(defaultMQAdminExt, "DefaultMQAdminExt");
        Preconditions.checkNotNull(brokerAddress, "BrokerAddress");

        Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
        if (null == broker) {
            logger.error("[MONITOR][CONSUMER-GROUP-SCHEDULER]Broker Sync may has error. Detecting a non-existing broker");
            return;
        }

        SubscriptionGroupWrapper subscriptionGroupWrapper = defaultMQAdminExt.fetchAllSubscriptionGroups(brokerAddress, 12000L);

        for (SubscriptionGroupConfig subscriptionGroupConfig : subscriptionGroupWrapper.getSubscriptionGroupTable().values()) {
            ConsumerGroup consumerGroup = cockpitConsumerGroupDBService.get(0, subscriptionGroupConfig.getGroupName());
            if (null == consumerGroup) {
                // First create this consumer group in database.
                consumerGroup = CockpitConsumerGroupMQServiceImpl.reWrap(subscriptionGroupConfig, broker.getClusterName());
                cockpitConsumerGroupDBService.insert(consumerGroup, 1);
            }

            if (consumerGroup.getStatus() == Status.DELETED) {
                cockpitConsumerGroupDBService.activate(consumerGroup.getId());
                warningMapper.create(WarnMsgHelper.makeWarning(Level.CRITICAL, "this group is create by nobody, broker not delete it? group:" + consumerGroup.getGroupName()));
            }

            if (cockpitBrokerDBService.hasConsumerGroup(broker.getId(), consumerGroup.getId())) {
                cockpitConsumerGroupDBService.refresh(broker.getId(), consumerGroup.getId());
            } else {
                cockpitBrokerDBService.createConsumerGroup(broker.getId(), consumerGroup.getId());
            }
        }
    }

    private void syncUpConsumerGroupsByBroker(DefaultMQAdminExt defaultMQAdminExt, String brokerAddress, Set<Long> groupIds) {
        Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
        List<ConsumerGroupHosting> endangeredConsumerGroupHostingList = new ArrayList<>();
        //add last update consumer group
//        endangeredConsumerGroupHostingList.addAll(cockpitConsumerGroupDBService.listEndangeredConsumerGroupsByBroker(broker.getId()));
        //add approved consumer group
        endangeredConsumerGroupHostingList.addAll(cockpitConsumerGroupDBService.listApprovedConsumerGroupsByBroker(broker.getId()));

        for (ConsumerGroupHosting hosting : endangeredConsumerGroupHostingList) {
            SubscriptionGroupConfig subscriptionGroupConfig = CockpitConsumerGroupMQServiceImpl.wrap(hosting.getConsumerGroup());
            try {
                logger.debug("[MONITOR][CONSUMER-GROUP-SCHEDULER]About to create consumer group {} on broker {}",
                        subscriptionGroupConfig.getGroupName(), brokerAddress);
                defaultMQAdminExt.createAndUpdateSubscriptionGroupConfig(brokerAddress, subscriptionGroupConfig, 15000L);
                groupIds.add(hosting.getConsumerGroup().getId());
                logger.info("[MONITOR][CONSUMER-GROUP-SCHEDULER]Consumer Group {} has been created on broker {}",
                        subscriptionGroupConfig.getGroupName(), brokerAddress);
            } catch (RemotingException | MQBrokerException | MQClientException | InterruptedException e) {
                logger.error("[MONITOR][CONSUMER-GROUP-SCHEDULER]Failed to create subscription group", e);
                String msg = "Failed to create consumer group " + hosting.getConsumerGroup().getGroupName() + " on broker " +  brokerAddress;
                warningMapper.create(WarnMsgHelper.makeWarning(Level.CRITICAL, msg));
            }
        }
    }

}
