package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownTopicCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by robert on 2015/6/9.
 */
public class TopicScheduler {
    private Logger logger = LoggerFactory.getLogger(TopicScheduler.class);

    @Autowired
    private DownTopicCommand downTopicCommand;

    @Scheduled(cron = "0 0 16 * * *")
    public void downloadTopic(){
        downTopicCommand.execute(null, null, null);
    }

    /**
     * Check broker status every 5 minutes.
     */
    @Scheduled(fixedRate = 30000)
    public void checkBrokerStatus() {

    }
}
