package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownTopicCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by robert on 2015/6/9.
 */
@Component
public class TopicScheduler {
    private Logger logger = LoggerFactory.getLogger(TopicScheduler.class);

    @Autowired
    private DownTopicCommand downTopicCommand;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @Autowired
    private CockpitBrokerService cockpitBrokerService;

    @Scheduled(cron = "24 12 * * * *")
    public void downloadTopic() {
        downTopicCommand.execute(null, null, null);
    }

    /**
     * check topic status every 5 minutes
     */
    @Scheduled(fixedRate = 30000)
    public void checkTopicStatus() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();

            Set<String> brokers = cockpitBrokerService.getALLBrokers(defaultMQAdminExt);
            List<Topic> topics = cockpitTopicService.getActiveTopics();
            for (Topic topic : topics) {
                Set<Long> teamIds = cockpitTopicService.getTeamId(topic);
                logger.info("[topic status check] this topic " + topic.getTopic() + " belongs to " + teamIds);
                //现阶段可对应的Broker与Topic信息不做处理
                if (brokers.contains(topic.getBrokerAddress()))
                    continue;

                //注销已有激活信息
                cockpitTopicService.unregister(topic.getId());
                //确认该Topic是否具有其他消费Broker
                if (cockpitTopicService.getTopic(topic.getTopic()).isEmpty()){
                    TopicConfig topicConfig = cockpitTopicService.getTopicConfigByTopicName(defaultMQAdminExt, topic.getTopic());
                    for (String broker : brokers){
                        topic.setBrokerAddress(broker);
                        cockpitTopicService.rebuildTopicConfig(defaultMQAdminExt, topicConfig, broker);

                        for (long teamId:teamIds)
                            cockpitTopicService.insert(topic, teamId);
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
