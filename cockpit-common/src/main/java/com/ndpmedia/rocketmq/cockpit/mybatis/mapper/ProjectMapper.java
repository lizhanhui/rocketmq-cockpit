package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Project;

import java.util.List;

public interface ProjectMapper {

    List<Project> list(long teamId);

    void create(Project project);

    void update(Project project);

    void delete(long id);
}
