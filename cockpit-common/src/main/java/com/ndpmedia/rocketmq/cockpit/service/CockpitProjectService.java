package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.Project;

import java.util.List;
import java.util.Set;

public interface CockpitProjectService {

    List<Project> list(long teamId);

    /**
     * add project to database by team
     * @param project project
     */
    void insert(Project project);

    /**
     * add project and consumer group ref
     * add project and topic ref
     * @param project   project name
     * @param consumerGroup consumer group name
     * @param topic topic name
     */
    void addRef(String project, String consumerGroup, String topic);

    /**
     * del project from database
     * @param projectId project id
     */
    void remove(long projectId);

    /**
     * get project by id or name
     * @param projectId id
     * @param projectName   name
     * @return  project
     */
    Project get(long projectId, String projectName);

    Set<String> getConsumerGroups(String projectName);

    Set<String> getTopics(String projectName);

}
