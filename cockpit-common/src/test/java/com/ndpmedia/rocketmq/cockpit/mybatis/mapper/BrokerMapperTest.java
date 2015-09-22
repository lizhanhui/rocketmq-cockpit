package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Broker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class BrokerMapperTest {

    @Autowired
    private BrokerMapper brokerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void insertBroker(long brokerId) {
        jdbcTemplate.update("INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, last_update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", brokerId, "DefaultCluster", "test-broker", 0, "localhost:10911", "3.2.2", 1, new Date());
    }

    private void deleteBroker(long brokerId) {
        jdbcTemplate.update("DELETE FROM broker WHERE id = ?", brokerId);
    }

    @Test
    public void testGet() throws Exception {
        long brokerId = 10000;
        insertBroker(brokerId);
        Broker broker = brokerMapper.get(brokerId, null);

        Assert.assertNotNull(broker);
        Assert.assertEquals("DefaultCluster", broker.getClusterName());
        Assert.assertEquals("test-broker", broker.getBrokerName());
        Assert.assertEquals(0, broker.getBrokerId());
        Assert.assertEquals("localhost:10911", broker.getAddress());
        Assert.assertEquals("3.2.2", broker.getVersion());
        Assert.assertEquals(1, broker.getDc());


        broker = brokerMapper.get(0, "localhost:10911");
        Assert.assertNotNull(broker);
        Assert.assertEquals("DefaultCluster", broker.getClusterName());
        Assert.assertEquals("test-broker", broker.getBrokerName());
        Assert.assertEquals(0, broker.getBrokerId());
        Assert.assertEquals("localhost:10911", broker.getAddress());
        Assert.assertEquals("3.2.2", broker.getVersion());
        Assert.assertEquals(1, broker.getDc());


        deleteBroker(brokerId);
    }
}