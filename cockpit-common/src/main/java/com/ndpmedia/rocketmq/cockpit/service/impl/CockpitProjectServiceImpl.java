package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ProjectMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("cockpitProjectService")
public class CockpitProjectServiceImpl implements CockpitProjectService {

    private Logger logger = LoggerFactory.getLogger(CockpitProjectServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<Project> list(long teamId) {
        return projectMapper.list(teamId);
    }

    @Override
    public void insert(Project project) {
        projectMapper.create(project);
    }

    @Override
    public void addRef(String project, String consumerGroup, String topic) {
        if (null != consumerGroup && !consumerGroup.isEmpty() && !consumerGroup.equals("$EMPTY$"))
            projectMapper.createRefC(project, consumerGroup);
        if (null != topic && !topic.isEmpty() && !topic.equals("$EMPTY$"))
            projectMapper.createRefT(project, topic);
    }

    @Transactional
    @Override
    public void remove(long projectId) {
        Project project = projectMapper.get(projectId, null);
        //TODO we use ID to build the reference
        projectMapper.delete(projectId);
        projectMapper.deleteC(project.getName());
        projectMapper.deleteT(project.getName());
    }

    @Override
    public Project get(long projectId, String projectName) {
        return projectMapper.get(projectId, projectName);
    }

    @Override
    public List<String> getConsumerGroups(String projectName) {
        return projectMapper.listC(projectName);
    }

    @Override
    public List<String> getTopics(String projectName) {
        return projectMapper.listT(projectName);
    }
}
