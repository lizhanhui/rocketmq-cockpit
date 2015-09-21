package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumeProgressNSService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupNSService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * consumer group base offset scheduler
 */
@Component
public class TaskScheduler {

    private Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private CockpitConsumeProgressNSService cockpitConsumeProgressNSService;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    @Autowired
    private CockpitConsumerGroupNSService cockpitConsumerGroupNSService;

    private static AtomicInteger counts = new AtomicInteger(0);

    /**
     * schedule:get consumer group and the topic offset.
     * period:300 second
     */
    @Scheduled(fixedRate = 300000)
    public void queryAccumulation() {
        Date date = new Date();
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();
            Set<String> groupList = cockpitConsumerGroupNSService.getGroups(defaultMQAdminExt);

            if (groupList.size() > counts.get()){

            }

            List<ConsumeProgress> consumeProgressList;
            for (String group : groupList) {
                consumeProgressList = cockpitConsumeProgressNSService.queryConsumerProgress(defaultMQAdminExt, group, null, null);
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
        } finally {
            defaultMQAdminExt.shutdown();
        }
    }

    private void createPrivateTable(Set<String> groups){

    }

    private void updateConsumeProgressData(ConsumeProgress consumeProgress){

    }
}
