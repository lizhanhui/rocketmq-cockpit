package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Broker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class BrokerMapperTest {

    @Autowired
    private BrokerMapper brokerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void insertBroker(long brokerId) {

        jdbcTemplate.update("INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", brokerId, "DefaultCluster", "test-broker", 0, "localhost:10911", "3.2.2", 1, new Date());
    }

    private void insertBroker(Broker broker) {
        jdbcTemplate.update("INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", broker.getId(), broker.getClusterName(), broker.getBrokerName(),
                broker.getBrokerId(), broker.getAddress(), broker.getVersion(), broker.getDc(), broker.getUpdateTime());

    }

    private void deleteBroker(long brokerId) {
        jdbcTemplate.update("DELETE FROM broker WHERE id = ?", brokerId);
    }

    @Before
    public void cleanBrokerTable() {
        jdbcTemplate.execute("DELETE FROM broker");
    }

    @Test
    public void testGet() throws Exception {
        long brokerId = 10000;
        insertBroker(brokerId);
        Broker broker = brokerMapper.get(brokerId);

        Assert.assertNotNull(broker);
        Assert.assertEquals("DefaultCluster", broker.getClusterName());
        Assert.assertEquals("test-broker", broker.getBrokerName());
        Assert.assertEquals(0, broker.getBrokerId());
        Assert.assertEquals("localhost:10911", broker.getAddress());
        Assert.assertEquals("3.2.2", broker.getVersion());
        Assert.assertEquals(1, broker.getDc());


        broker = brokerMapper.getBrokerByAddress("localhost:10911");
        Assert.assertNotNull(broker);
        Assert.assertEquals("DefaultCluster", broker.getClusterName());
        Assert.assertEquals("test-broker", broker.getBrokerName());
        Assert.assertEquals(0, broker.getBrokerId());
        Assert.assertEquals("localhost:10911", broker.getAddress());
        Assert.assertEquals("3.2.2", broker.getVersion());
        Assert.assertEquals(1, broker.getDc());


        deleteBroker(brokerId);
    }


    @Test
    public void testList() {
        int total = 1000;
        List<Broker> brokers = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Broker broker = new Broker();
            broker.setClusterName("DefaultCluster" + (i % 2));
            broker.setAddress("address_" + i + ":10911");
            broker.setBrokerId(i % 2);
            broker.setBrokerName("TestBroker" + i);
            broker.setVersion("3.2.2");
            broker.setDc(i % 5 + 1);
            broker.setId(10000 + i);
            broker.setUpdateTime(new Date());
            broker.setCreateTime(new Date());
            brokers.add(broker);
            insertBroker(broker);
        }


        List<Broker> brokerList = brokerMapper.list(null, null, -1, -1, null);
        Assert.assertEquals(total, brokerList.size());

        brokerList = brokerMapper.list("DefaultCluster0", null, -1, -1, null);
        Assert.assertEquals(total / 2, brokerList.size());

        for (Broker broker : brokers) {
            deleteBroker(broker.getId());
        }
    }
}