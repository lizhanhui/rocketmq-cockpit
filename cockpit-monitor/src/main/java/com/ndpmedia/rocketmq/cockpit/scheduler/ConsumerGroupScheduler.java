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
     * period:one hour(20:24 of an hour)
     */
    @Scheduled(cron = "24 20 * * * *")
    public void synchronizeConsumerGroups() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();
            Set<String> brokerAddresses = cockpitBrokerMQService.getALLBrokers(defaultMQAdminExt);

            for (String brokerAddress : brokerAddresses) {
                syncDownConsumerGroupsByBroker(defaultMQAdminExt, brokerAddress);
                syncUpConsumerGroupsByBroker(defaultMQAdminExt, brokerAddress);
            }

        } catch (Exception e) {
            logger.error("Failed to synchronizeConsumerGroups", e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
    }

    private void syncDownConsumerGroupsByBroker(DefaultMQAdminExt defaultMQAdminExt, String brokerAddress)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Preconditions.checkNotNull(defaultMQAdminExt, "DefaultMQAdminExt");
        Preconditions.checkNotNull(brokerAddress, "BrokerAddress");

        Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
        if (null == broker) {
            logger.error("Broker Sync may has error. Detecting a non-existing broker");
            return;
        }

        SubscriptionGroupWrapper subscriptionGroupWrapper = defaultMQAdminExt.fetchAllSubscriptionGroups(brokerAddress, 3000);

        for (SubscriptionGroupConfig subscriptionGroupConfig : subscriptionGroupWrapper.getSubscriptionGroupTable().values()) {
            ConsumerGroup consumerGroup = cockpitConsumerGroupDBService.get(0, subscriptionGroupConfig.getGroupName());
            if (null == consumerGroup) {
                // First create this consumer group in database.
                consumerGroup = new ConsumerGroup();
                consumerGroup.setClusterName(broker.getClusterName());
                consumerGroup.setGroupName(subscriptionGroupConfig.getGroupName());
                consumerGroup.setConsumeEnable(subscriptionGroupConfig.isConsumeEnable());
                consumerGroup.setConsumeBroadcastEnable(subscriptionGroupConfig.isConsumeBroadcastEnable());
                consumerGroup.setConsumeFromBrokerId((int)subscriptionGroupConfig.getBrokerId());
                consumerGroup.setWhichBrokerWhenConsumeSlowly((int)subscriptionGroupConfig.getWhichBrokerWhenConsumeSlowly());
                consumerGroup.setCreateTime(new Date());
                consumerGroup.setUpdateTime(new Date());
                consumerGroup.setStatus(Status.ACTIVE);
                cockpitConsumerGroupDBService.insert(consumerGroup, 1);
            }

            if (cockpitBrokerDBService.hasConsumerGroup(broker.getId(), consumerGroup.getId())) {
                cockpitConsumerGroupDBService.refresh(broker.getId(), consumerGroup.getId());
            } else {
                cockpitBrokerDBService.createConsumerGroup(broker.getId(), consumerGroup.getId());
            }
        }
    }

    private void syncUpConsumerGroupsByBroker(DefaultMQAdminExt defaultMQAdminExt, String brokerAddress) {
        Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
        List<ConsumerGroupHosting> endangeredConsumerGroupHostingList = cockpitConsumerGroupDBService.listEndangeredConsumerGroupsByBroker(broker.getId());
        for (ConsumerGroupHosting hosting : endangeredConsumerGroupHostingList) {
            SubscriptionGroupConfig subscriptionGroupConfig = CockpitConsumerGroupMQServiceImpl.wrap(hosting.getConsumerGroup());
            try {
                logger.debug("About to create consumer group {} on broker {}",
                        subscriptionGroupConfig.getGroupName(), brokerAddress);
                defaultMQAdminExt.createAndUpdateSubscriptionGroupConfig(brokerAddress, subscriptionGroupConfig);
                logger.info("Consumer Group {} has been created on broker {}",
                        subscriptionGroupConfig.getGroupName(), brokerAddress);
            } catch (RemotingException | MQBrokerException | MQClientException | InterruptedException e) {
                logger.error("Failed to create subscription group", e);
                Warning warning = new Warning();
                warning.setCreateTime(new Date());
                warning.setStatus(Status.ACTIVE);
                warning.setLevel(Level.CRITICAL);
                warning.setMsg("Failed to create consumer group " + hosting.getConsumerGroup().getGroupName() + " on broker " +  brokerAddress);
                warningMapper.create(warning);
            }
        }
    }

}
