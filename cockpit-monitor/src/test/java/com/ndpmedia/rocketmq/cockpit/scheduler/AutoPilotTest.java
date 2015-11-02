package com.ndpmedia.rocketmq.cockpit.scheduler;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoPilotTest {

    @Autowired
    private AutoPilot autoPilot;

    @Autowired
    private ConsumerGroupScheduler consumerGroupScheduler;

    @Autowired
    private TopicScheduler topicScheduler;

    @Autowired
    private BrokerScheduler brokerScheduler;

    @BeforeClass
    public static void setUp() {
        System.setProperty("enable_ssl", "true");
        System.setProperty("rocketmq.namesrv.domain", "172.30.30.125");
        System.setProperty("log.home", "/var/tmp");
    }

    @Test
    public void testSetUp() {
        Assert.assertNotNull(autoPilot);
    }

    @Test
    public void testAutoPilot() throws Exception {
        brokerScheduler.synchronizeBrokers();
        topicScheduler.synchronizeTopics();
        consumerGroupScheduler.synchronizeConsumerGroups();
        autoPilot.autoPilot();
    }
}