package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ProjectMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("cockpitProjectService")
public class CockpitProjectServiceImpl implements CockpitProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CockpitProjectServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public List<Project> list(long teamId) {
        return projectMapper.list(teamId);
    }

    @Override
    public void insert(Project project) {
        projectMapper.create(project);
    }

    @Override
    public void addTopic(long projectId, long topicId) {
        topicMapper.connectProject(topicId, projectId);
    }

    @Override
    public void addConsumerGroup(long projectId, long consumerGroupId) {
        consumerGroupMapper.connectProject(consumerGroupId, projectId);
    }

    @Transactional
    @Override
    public void remove(long projectId) {
        topicMapper.disconnectProject(0, projectId);
        consumerGroupMapper.disconnectProject(0, projectId);
        projectMapper.delete(projectId);
    }

    @Override
    public Project get(long projectId, String projectName) {
        return projectMapper.get(projectId, projectName);
    }

    @Override
    public List<ConsumerGroup> getConsumerGroups(long projectId) {
        return consumerGroupMapper.list(projectId, null, null, 0, null, 30);
    }

    @Override
    public List<ConsumerGroup> getUnuseConsumerGroups(long projectId) {
        return consumerGroupMapper.listByOtherProject(projectId);
    }

    @Override
    public List<TopicMetadata> getTopics(long projectId) {
        return topicMapper.list(projectId, null, null, 30);
    }

    @Override
    public List<TopicMetadata> getUnuseTopics(long projectId) {
        return topicMapper.listByOtherProject(projectId);
    }
}
