package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = "classpath:applicationContextCommon.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectMapperTest {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testSetUp() {
        Assert.assertNotNull(projectMapper);
        Assert.assertNotNull(jdbcTemplate);
    }

    @Test
    public void testList() throws Exception {
        List<Project> projectList = projectMapper.list(1);
        Assert.assertFalse(projectList.isEmpty());

        Project project = projectList.get(0);
        for (TopicMetadata topicMetadata : project.getTopics()) {
            System.out.println(topicMetadata.getTopic());
        }


        for (ConsumerGroup consumerGroup : project.getConsumerGroups()) {
            System.out.println(consumerGroup.getGroupName());
        }

    }


    @Test
    public void testList2() {
        List<Project> projects = projectMapper.list(0);
        Assert.assertTrue(projects.size() >= 2);
    }

    @Test
    public void testCreate() throws Exception {
        Project project = new Project();
        project.setName("Test Team");
        project.setTeamId(1);
        project.setDescription("Desc");
        projectMapper.create(project);

        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT COUNT(1) AS cnt FROM project WHERE id = ?", project.getId());
        Assert.assertTrue((Integer.parseInt(list.get(0).get("cnt").toString()) > 0));

        jdbcTemplate.update("DELETE FROM project WHERE id = ?", project.getId());
    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testGet() throws Exception {
        Project project = projectMapper.get(1, null);
        Assert.assertNotNull(project);
    }
}