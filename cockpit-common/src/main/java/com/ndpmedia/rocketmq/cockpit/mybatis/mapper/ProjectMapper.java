package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectMapper {

    List<Project> list(@Param("teamId") long teamId);

    void create(Project project);

    void update(Project project);

    void delete(@Param("id") long id);

    Project get(@Param("projectId") long projectId);
}
