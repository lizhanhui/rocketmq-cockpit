package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.TopicSyncDownCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicRocketMQService;
import com.ndpmedia.rocketmq.cockpit.util.TopicTranslate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    private CockpitTopicRocketMQService cockpitTopicRocketMQService;

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

            Set<String> brokers = cockpitBrokerService.getALLBrokers(defaultMQAdminExt);
            List<Topic> topics = cockpitTopicDBService.getTopics(Status.ACTIVE);
            for (Topic topic : topics) {
                //现阶段可对应的Broker与Topic信息不做处理
                if (brokers.isEmpty() || brokers.contains(topic.getBrokerAddress()))
                    continue;

                //注销已有激活信息
                cockpitTopicDBService.deactivate(topic.getId());
                //确认该Topic是否具有其他Broker
                if (!cockpitTopicDBService.exists(topic.getTopic())){
                    List<Long> projectIds = cockpitTopicDBService.getProjectIDs(topic.getId(), null);
                    logger.info("[topic status check] this topic " + topic.getTopic() + " belongs to " + Arrays.toString(projectIds.toArray()));
                    //topic route信息可能无法获得，导致topic config无法获取broker端版本，使用数据库端版本构建
                    TopicConfig topicConfig = cockpitTopicRocketMQService.getTopicConfigByTopicName(defaultMQAdminExt, topic.getTopic());
                    if (null == topicConfig)
                        topicConfig = TopicTranslate.wrap(topic);

                    for (String broker : brokers){
                        topic.setBrokerAddress(broker);
                        cockpitTopicRocketMQService.rebuildTopicConfig(defaultMQAdminExt, topicConfig, broker);
                        if (!projectIds.isEmpty()) {
                            for (long projectId :projectIds) {
                                cockpitTopicDBService.insert(topic, projectId);
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
