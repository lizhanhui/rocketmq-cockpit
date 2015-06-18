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

    @Transactional
    @Override
    public void remove(long topicId) {
        projectMapper.delete(topicId);
    }

    @Override
    public Project get(long projectId, String projectName) {
        return projectMapper.get(projectId, projectName);
    }
}
