package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumeProgressService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * topic base offset scheduler
 */
@Component
public class TopicProgressScheduler {

    private Logger logger = LoggerFactory.getLogger(TopicProgressScheduler.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private CockpitConsumeProgressService cockpitConsumeProgressService;

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

    private static Date date = new Date();

    /**
     * schedule:get consumer group and the topic offset.
     * period:300 second
     */
    @Scheduled(fixedRate = 120000)
    public void queryAccumulation() {
        try {
            date = consumeProgressMapper.lastrow().get(1).getCreateTime();
            if (consumeProgressMapper.topicReady(date).size() > 0) {
                logger.info("[MONITOR][TOPIC PROGRESS] last consume progress already analysed");
            }else {
                updateTopicProgressData();
                updateConsumerGroupTopics();
            }
        } catch (Exception e) {
            logger.warn("[MONITOR][TOPIC PROGRESS] main method failed." + e);

        }
    }

    private void updateTopicProgressData(){
        long num = consumeProgressMapper.updateTopicProgress(date);
        logger.info("[MONITOR][TOPIC PROGRESS] NOW WE UPDATE TOPIC PROGRESS : " + num);
    }

    private void updateConsumerGroupTopics(){
        long num = consumerGroupMapper.updateConsumerGroupTopics(date);
        logger.info("[MONITOR][TOPIC PROGRESS] NOW WE UPDATE CONSUMER GROUP AND TOPIC XREF : " + num);
    }
}
