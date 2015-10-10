package com.ndpmedia.rocketmq.cockpit.scheduler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TopicSchedulerTest {

    @Autowired
    private TopicScheduler topicScheduler;

    @Autowired
    private BrokerScheduler brokerScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() {
        System.setProperty("enable_ssl", "true");
        System.setProperty("rocketmq.namesrv.domain", "172.30.30.125");
        System.setProperty("log.home", "/var/tmp");
    }

    @Test
    public void testCheckTopicStatus() throws Exception {
        brokerScheduler.checkBrokerStatus();
        for (int i = 0; i < 3; i++) {
            try {
                topicScheduler.checkTopicStatus();
                Thread.sleep(3 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}