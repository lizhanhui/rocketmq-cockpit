package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.QueueData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.TopicSyncDownCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private TopicSyncDownCommand topicSyncDownCommand;

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private CockpitBrokerMQService cockpitBrokerMQService;

    /**
     * check topic status every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void checkTopicStatus() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();

            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            if (null != topicList && !topicList.getTopicList().isEmpty()) {
                for (String topic : topicList.getTopicList()) {

                    // We skip retry and deletion topics.
                    if (topic.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX)
                            || topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                        continue;
                    }

                    TopicRouteData topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                    Topic topicEntity = cockpitTopicDBService.getTopic(topic);
                    if (null == topicEntity) {
                        topicEntity = new Topic();
                        topicEntity.setTopic(topic);
                        topicEntity.setCreateTime(new Date());
                        topicEntity.setUpdateTime(new Date());
                        topicEntity.setClusterName(getClusterName(topicRouteData.getBrokerDatas()));
                        cockpitTopicDBService.insert(topicEntity);

                        // Add it to default project for now.
                        cockpitTopicDBService.insertTopicProjectInfo(topicEntity.getId(), 1);
                    }

                    for (QueueData queueData: topicRouteData.getQueueDatas()) {
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

                        if (null != broker && !cockpitBrokerDBService.hasTopic(broker.getId(), topicEntity.getId())) {
                            topicEntity.setReadQueueNum(queueData.getReadQueueNums());
                            topicEntity.setWriteQueueNum(queueData.getWriteQueueNums());
                            topicEntity.setPermission(queueData.getPerm());
                            cockpitTopicDBService.insertTopicBrokerInfo(topicEntity, broker.getId());
                        } else if (null != broker) {
                            cockpitTopicDBService.refresh(broker.getId(), topicEntity.getId());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            defaultMQAdminExt.shutdown();
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
}
