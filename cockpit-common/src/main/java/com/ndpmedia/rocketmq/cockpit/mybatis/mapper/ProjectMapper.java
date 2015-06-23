package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectMapper {

    List<Project> list(@Param("teamId") long teamId);

    void create(Project project);

    void createRefC(@Param("project") String project, @Param("consumerGroup") String consumerGroup);

    void createRefT(@Param("project") String project, @Param("topic") String topic);

    void update(Project project);

    void delete(long id);

    void deleteC(String name);

    void deleteT(String name);

    Project get(@Param("projectId") long projectId, @Param("projectName") String projectName);
}
