package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
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
public class BrokerSchedulerTest {

    @Autowired
    private BrokerScheduler brokerScheduler;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() {
        System.setProperty("enable_ssl", "true");
        System.setProperty("rocketmq.namesrv.domain", "172.30.30.125");
        System.setProperty("log.home", "/var/tmp");
    }

    @Test
    public void testSetup() {
        Assert.assertNotNull(brokerScheduler);
    }

    private void clean() {
        jdbcTemplate.execute("DELETE FROM broker");
    }

    @Test
    public void testCheckBrokerStatus() {
        for (int i = 0; i < 3; i++) {
            brokerScheduler.syncBrokerStatus();
            Assert.assertFalse(cockpitBrokerDBService.list(null, null, 0, 0).isEmpty());
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }
    }

    @After
    public void tearDown() {
        clean();
    }

}