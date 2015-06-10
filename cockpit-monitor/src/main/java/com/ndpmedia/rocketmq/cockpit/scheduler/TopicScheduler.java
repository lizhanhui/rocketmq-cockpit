package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownTopicCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
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

    @Scheduled(cron = "24 12 * * * *")
    public void downloadTopic(){
        downTopicCommand.execute(null, null, null);
    }

    /**
     * check topic status every 5 minutes
     */
    @Scheduled(fixedRate = 30000)
    public void checkTopicStatus(){
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
