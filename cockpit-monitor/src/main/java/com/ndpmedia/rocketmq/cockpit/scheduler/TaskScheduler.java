package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.common.MixAll;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitMessageMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.LoginMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumeProgressService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class TaskScheduler {

    private Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private CockpitMessageMapper cockpitMessageMapper;

    @Autowired
    private CockpitConsumeProgressService cockpitConsumeProgressService;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @Scheduled(fixedRate = 300000)
    public void queryAccumulation() {

        Date date = new Date();
        try {
            Set<String> topicList = cockpitTopicService.fetchTopics();

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

    @Scheduled(cron = "0 0 * * * *")
    public void deleteDeprecatedData() {
        logger.info("Start to clean deprecated data");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int numberOfRecordsDeleted = consumeProgressMapper.bulkDelete(calendar.getTime());
        logger.info("Deleted " + numberOfRecordsDeleted + " consume progress records.");

        numberOfRecordsDeleted = cockpitMessageMapper.bulkDelete(calendar.getTime());
        logger.info("Deleted " + numberOfRecordsDeleted + " message flow records.");
    }


    @Scheduled(fixedRate = 30000)
    public void deleteDeprecatedLoginData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        loginMapper.delete(calendar.getTime());
    }

}
