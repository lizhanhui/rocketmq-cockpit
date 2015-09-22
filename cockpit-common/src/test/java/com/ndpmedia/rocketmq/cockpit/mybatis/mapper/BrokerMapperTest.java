package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class BrokerMapperTest {

    @Autowired
    private BrokerMapper brokerMapper;

    @Test
    public void testGet() throws Exception {
        Assert.assertNotNull(brokerMapper);
    }
}