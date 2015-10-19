package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroupHosting;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerGroupMapperTest {

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testSetUp() {
        Assert.assertNotNull(jdbcTemplate);
        Assert.assertNotNull(consumerGroupMapper);
    }

    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testListByTopic() throws Exception {

    }

    @Test
    public void testListByProject() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testGetByName() throws Exception {

    }

    @Test
    public void testRefresh() throws Exception {

    }

    @Test
    public void testInsert() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testQueryHosting() throws Exception {
        List<ConsumerGroupHosting> hostingList = consumerGroupMapper.queryHosting(0, null, 0, 100, null);
    }

    @Test
    public void testDisconnectProject() throws Exception {

    }

    @Test
    public void testConnectProject() throws Exception {

    }

    @Test
    public void testQueryEndangeredHosting() {
        List<ConsumerGroupHosting> endangeredConsumerGroupHostingList = consumerGroupMapper.queryEndangeredHosting(0);
    }
}