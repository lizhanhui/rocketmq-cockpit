package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.QueueData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.*;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.WarningMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import com.ndpmedia.rocketmq.cockpit.util.TopicTranslate;
import com.ndpmedia.rocketmq.cockpit.util.WarnMsgHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by robert on 2015/6/9.
 * check topic and topic status
 * 1.auto download topic from cluster or broker to data base
 * 2.if broker break, auto check topic , create or update topic config on cluster or broker.
 */
@Component
public class TopicScheduler {
    private Logger logger = LoggerFactory.getLogger(TopicScheduler.class);

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private CockpitBrokerMQService cockpitBrokerMQService;

    @Autowired
    private WarningMapper warningMapper;

    private Set<String> brokerAddresses;

    /**
     * synchronize topics every 10 minutes
     */
    @Scheduled(fixedRate = 600000)
    public void synchronizeTopics() {
        logger.info("[MONITOR][TOPIC-SCHEDULER] scheduler start");
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();

            brokerAddresses = cockpitBrokerMQService.getALLBrokers(defaultMQAdminExt);
            syncDownTopics(defaultMQAdminExt);
            syncUpTopics(defaultMQAdminExt);
        } catch (MQClientException e) {
            logger.error("[MONITOR][TOPIC-SCHEDULER]Failed to synchronize topics", e);
        } finally {
            if (null != defaultMQAdminExt)
                defaultMQAdminExt.shutdown();
        }

        logger.info("[MONITOR][TOPIC-SCHEDULER] scheduler end");
    }

    private void syncDownTopics(DefaultMQAdminExt defaultMQAdminExt) {
        try {
            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            if (null != topicList && !topicList.getTopicList().isEmpty()) {
                for (String topic : topicList.getTopicList()) {

                    // We do not need to manage System topics.
                    if (TopicTranslate.isGroup(topic) || isDefault(topic)) {
                        continue;
                    }

                    TopicRouteData topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);

                    // For now, we only handle DefaultCluster.
                    TopicMetadata topicMetadata = cockpitTopicDBService.getTopic(Constants.DEFAULT_CLUSTER, topic);
                    if (null == topicMetadata) {
                        topicMetadata = TopicTranslate.createTopicMetadata(topic, getClusterName(topicRouteData.getBrokerDatas()));

                        cockpitTopicDBService.insert(topicMetadata);

                        // Add it to default project for now.
                        cockpitTopicDBService.insertTopicProjectInfo(topicMetadata.getId(), 1);
                    }

                    for (QueueData queueData: topicRouteData.getQueueDatas()) {
                        Broker broker = getBroker(topicRouteData, queueData);

                        if (null != broker && !cockpitBrokerDBService.hasTopic(broker.getId(), topicMetadata.getId())) {
                            TopicBrokerInfo topicBrokerInfo = TopicTranslate.createTopicBrokerInfo(broker, topicMetadata,
                                    queueData.getReadQueueNums(), queueData.getWriteQueueNums(), queueData.getPerm());

                            cockpitTopicDBService.insertTopicBrokerInfo(topicBrokerInfo);
                            if (!cockpitTopicDBService.isDCAllowed(topicMetadata.getId(), broker.getDc())) {
                                cockpitTopicDBService.addDCAllowed(topicMetadata.getId(), broker.getDc(), Status.ACTIVE);
                            }
                        } else if (null != broker) {
                            cockpitTopicDBService.refreshTopicBrokerInfo(topicMetadata.getId(), broker.getId());
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("[MONITOR][TOPIC-SCHEDULER]Failed to sync topic", e);
        }
    }

    private Broker getBroker(TopicRouteData topicRouteData, QueueData queueData) {
        Broker broker = null;
        for (BrokerData brokerData : topicRouteData.getBrokerDatas()) {
            if (brokerData.getBrokerName().equals(queueData.getBrokerName())) {
                for (Map.Entry<Long, String> entry : brokerData.getBrokerAddrs().entrySet()) {
                    if (entry.getKey() == MixAll.MASTER_ID) {
                        broker = cockpitBrokerDBService.get(0, entry.getValue());
                        if (null != broker) {
                            break;
                        }
                    }
                }

                if (null == broker) {
                    for (Map.Entry<Long, String> entry : brokerData.getBrokerAddrs().entrySet()) {
                        broker = cockpitBrokerDBService.get(0, entry.getValue());
                        if (null != broker) {
                            break;
                        }
                    }
                }
            }
        }
        return broker;
    }

    private void syncUpTopics(DefaultMQAdminExt defaultMQAdminExt) {
        for (String brokerAddress : brokerAddresses) {
            Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
            List<TopicBrokerInfo> list = new ArrayList<>();
            //add last update topic
//            list.addAll(cockpitTopicDBService.queryEndangeredTopicBrokerInfoList(broker.getId()));
            //add approved topic
            list.addAll(cockpitTopicDBService.queryApprovedTopicsByBroker(broker.getId()));
            for (TopicBrokerInfo topicBrokerInfo : list) {
                TopicConfig topicConfig = TopicTranslate.wrapTopicToTopicConfig(topicBrokerInfo);
                try {
                    logger.debug("[MONITOR][TOPIC-SCHEDULER]About to create topic {} on broker {}",
                            topicConfig.getTopicName(), brokerAddress);
                    defaultMQAdminExt.createAndUpdateTopicConfig(brokerAddress, topicConfig, 15000L);
                    cockpitTopicDBService.activate(topicBrokerInfo.getTopicMetadata().getId(), topicBrokerInfo.getBroker().getId());
                    logger.info("[MONITOR][TOPIC-SCHEDULER]Topic {} has been created on broker {}",
                            topicConfig.getTopicName(), brokerAddress);
                } catch (RemotingException | MQBrokerException | MQClientException | InterruptedException e) {
                    logger.error("[MONITOR][TOPIC-SCHEDULER]Failed to create topic {} on broker {}", topicConfig.getTopicName(),
                            brokerAddress);
                    String msg = String.format("Failed to create topic %s on broker %s", topicConfig.getTopicName(), brokerAddress);
                    warningMapper.create(WarnMsgHelper.makeWarning(Level.CRITICAL, msg));
                }
            }

        }

    }

    private String getClusterName(List<BrokerData> brokerDataList) {
        for (BrokerData brokerData : brokerDataList) {
            for (Map.Entry<Long, String> entry : brokerData.getBrokerAddrs().entrySet()) {
                if (entry.getKey() == MixAll.MASTER_ID) {
                    String brokerAddress = entry.getValue();
                    Broker broker = cockpitBrokerDBService.get(0, brokerAddress);
                    return broker.getClusterName();
                }
            }
        }
        throw new RuntimeException("Unable to figure out cluster name by broker address");
    }

    public boolean isDefault(String topic) {
        if (brokerAddresses.contains(topic))
            return true;
        return false;
    }
}
