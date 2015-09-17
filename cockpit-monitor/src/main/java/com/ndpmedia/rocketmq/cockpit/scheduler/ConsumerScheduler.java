package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownConsumerCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by robert on 2015/6/9.
 * check consumer group
 * 1.auto download consumer group from cluster or broker to data base
 * 2. auto check consumer group , create or update consumer group config on cluster or broker.
 */
@Component
public class ConsumerScheduler {
    private Logger logger = LoggerFactory.getLogger(ConsumerScheduler.class);

    @Autowired
    private DownConsumerCommand downConsumerCommand;

    @Autowired
    private CockpitConsumerGroupService cockpitConsumerGroupService;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    /**
     * schedule:check consumer group from cluster and broker.
     * period:one hour(16:24 of an hour)
     */
    @Scheduled(cron = "24 16 * * * *")
    public void downloadTopic() {
        downConsumerCommand.execute(null, null, null);
    }

    /**
     * update consumer group to cluster
     * period:one hour(20:24 of an hour)
     */
    @Scheduled(cron = "24 20 * * * *")
    public void checkTopicStatus() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();

            List<ConsumerGroup> consumerGroups = consumerGroupMapper.list(0, null, null, null);
            for (ConsumerGroup consumerGroup:consumerGroups){
                if (consumerGroup.getStatus() != Status.ACTIVE)
                    continue;

                cockpitConsumerGroupService.update(consumerGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
