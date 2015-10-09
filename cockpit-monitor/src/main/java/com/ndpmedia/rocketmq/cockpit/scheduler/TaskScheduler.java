package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.MixAll;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumeProgressService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * consumer group base offset scheduler
 */
@Component
public class TaskScheduler {

    private Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private CockpitConsumeProgressService cockpitConsumeProgressService;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    /**
     * schedule:get consumer group and the topic offset.
     * period:300 second
     */
    @Scheduled(fixedRate = 300000)
    public void queryAccumulation() {
        Date date = new Date();
        try {
            Set<String> topicList = cockpitTopicMQService.fetchAllTopics(null, true);

            List<ConsumeProgress> consumeProgressList;
            for (String topic : topicList) {
                if (!topic.contains(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                    //All operational consumer groups have a topic with pattern: %RETRY%_{ConsumerGroup}
                    continue;
                }

                consumeProgressList = cockpitConsumeProgressService
                        .queryConsumerProgress(topic.replace(MixAll.RETRY_GROUP_TOPIC_PREFIX, ""), null, null);
                for (ConsumeProgress cp : consumeProgressList) {
                    if (null == cp || null == cp.getTopic() || null == cp.getBrokerName()) {
                        continue;
                    }
                    cp.setCreateTime(date);
                    consumeProgressMapper.insert(cp);
                }
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("offset table is empty")) {
                logger.warn("[MONITOR][CONSUME PROCESS] main method failed." + e);
            }
        }
    }
}
