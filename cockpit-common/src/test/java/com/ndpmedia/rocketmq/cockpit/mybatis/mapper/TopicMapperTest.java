package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Chair;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TopicMapperTest {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testIoC() {
        Assert.assertNotNull(topicMapper);
        Assert.assertNotNull(jdbcTemplate);
    }

    @Test
    public void testGetTopicMetadata() {
        long id = 10000;
        boolean deleteOnExit = false;
        if (jdbcTemplate.queryForList("SELECT * FROM topic WHERE id = ?", id).isEmpty()) {
            jdbcTemplate.update("INSERT INTO topic(id, topic, cluster_name, `order`, create_time, update_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)", id, "Test_TOPIC_" + id, "DefaultCluster", true, new Date(), new Date(), 1);
            deleteOnExit = true;
        }

        TopicMetadata topicMetadata = topicMapper.getMetadata(id);

         Assert.assertNotNull(topicMetadata);

        if (deleteOnExit) {
            jdbcTemplate.update("DELETE FROM topic WHERE id = ?", id);
        }
    }

    @Test
    public void testInsert() {
        Topic topic = new Topic();
        topic.setTopic("Test_Topic_Unit");
        topic.setClusterName("DefaultCluster");
        topic.setCreateTime(new Date());
        topic.setOrder(true);
        topic.setUpdateTime(new Date());
        topic.setStatus(Status.DRAFT);
        topicMapper.insert(topic);

        Assert.assertTrue(topic.getId() > 0);

        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT COUNT(1) FROM topic WHERE topic = ? AND cluster_name = ? AND `order` IS TRUE ", "Test_Topic_Unit", "DefaultCluster");
        Assert.assertTrue(!list.isEmpty());

        jdbcTemplate.execute("DELETE FROM topic WHERE topic = 'Test_Topic_Unit'");
    }


    @Test
    public void testQueryTopicBrokerInfo() {
        List<TopicBrokerInfo> topicBrokerInfoList = topicMapper.queryTopicBrokerInfo(2102, 0, 0);
        for (TopicBrokerInfo topicBrokerInfo : topicBrokerInfoList) {
            System.out.println(topicBrokerInfo.getStatus().getText());
        }
    }


    @Test
    public void testChair() {
        Chair chair = topicMapper.getChair(1);
        System.out.println(chair.getDesk().getName());
    }

}