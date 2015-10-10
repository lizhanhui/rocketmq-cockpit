package com.ndpmedia.rocketmq.cockpit.scheduler;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerGroupSchedulerTest {

    @Autowired
    private ConsumerGroupScheduler consumerGroupScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() {
        System.setProperty("enable_ssl", "true");
        System.setProperty("rocketmq.namesrv.domain", "172.30.30.125");
        System.setProperty("log.home", "/var/tmp");
    }

    @Test
    public void testSetUp() {
        Assert.assertNotNull(consumerGroupScheduler);
    }

    @Test
    public void testSyncConsumerGroupStatus() throws Exception {
        consumerGroupScheduler.syncConsumerGroupStatus();
    }


    @After
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM broker_consumer_group_xref");
        jdbcTemplate.execute("DELETE FROM project_consumer_group_xref");
        jdbcTemplate.execute("DELETE FROM consumer_group");
        jdbcTemplate.execute("DELETE FROM broker");
    }
}