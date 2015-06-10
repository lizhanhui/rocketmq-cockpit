package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.scheduler.command.DownTopicCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
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
    private CockpitBrokerService cockpitBrokerService;

    @Autowired
    private CockpitTopicService cockpitTopicService;

    private Set<String> baseBrokers = new HashSet<>();

    @Scheduled(cron = "24 12 * * * *")
    public void downloadTopic(){
        downTopicCommand.execute(null, null, null);
    }

    /**
     * Check broker status every 5 minutes.
     */
    @Scheduled(fixedRate = 30000)
    public void checkBrokerStatus() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            defaultMQAdminExt.start();
            Set<String> brokers =  cockpitBrokerService.getALLBrokers(defaultMQAdminExt);
            if (baseBrokers.isEmpty()) {
                logger.info("[check broker status] base broker is empty.");
                baseBrokers.addAll(brokers);
            }
            else {
                //ToDo use java 1.8 stream change this
                for (String broker:brokers){
                    if (!baseBrokers.contains(broker)){
                        logger.info("[check broker status] " + broker + " is new broker ");
                        baseBrokers.add(broker);
                    }
                }

                for (String broker:baseBrokers){
                    if (!brokers.contains(broker)){
                        logger.info("[check broker status] " + broker + " is already DEL.");
                        cockpitBrokerService.removeAllTopic(broker);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
