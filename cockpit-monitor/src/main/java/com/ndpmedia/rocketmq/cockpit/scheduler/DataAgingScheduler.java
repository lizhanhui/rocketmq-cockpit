package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.CockpitMessageMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumeProgressMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.LoginMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Created by robert on 2015/6/9.
 */
@Component
public class DataAgingScheduler {
    private Logger logger = LoggerFactory.getLogger(DataAgingScheduler.class);

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private CockpitMessageMapper cockpitMessageMapper;

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

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
