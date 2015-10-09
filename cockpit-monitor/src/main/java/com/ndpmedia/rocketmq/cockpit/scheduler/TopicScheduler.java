package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.TopicSyncDownCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private CockpitBrokerService cockpitBrokerService;

    /**
     * schedule:check topic and topic route from cluster and broker.
     * period:one hour(12:24 of an hour)
     */
    @Scheduled(cron = "24 12 * * * *")
    public void downloadTopic() {
        topicSyncDownCommand.execute(null, null, null);
    }

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
                    TopicRouteData topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                    Topic topicEntity = cockpitTopicDBService.getTopic(topic);
                    if (null == topicEntity) {
                        topicEntity = new Topic();


                    }

                    for (BrokerData brokerData : topicRouteData.getBrokerDatas()) {
                        for (Map.Entry<Long, String> next : brokerData.getBrokerAddrs().entrySet()) {
                            if (next.getKey() == 0) {
                                String brokerAddress = next.getValue();
                                Broker broker = cockpitBrokerService.get(0, brokerAddress);
                                if (null != broker) {
                                    // cockpitTopicDBService.refresh(broker.getId(), );
                                }
                            }
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
}
