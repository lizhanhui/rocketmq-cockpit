package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CockpitTopicDBServiceImplTest {

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @Test
    public void testSetUp() {
        Assert.assertNotNull(cockpitTopicDBService);
    }

    @Test
    public void testGetTopics() throws Exception {
        List<Topic> topics = cockpitTopicDBService.getTopics(Status.ACTIVE, Status.APPROVED);
        Assert.assertFalse(topics.isEmpty());
    }
}