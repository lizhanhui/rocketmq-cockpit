package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.Project;

public interface CockpitProjectService {

    /**
     * add project to database by team
     * @param project project
     */
    void insert(Project project);

    /**
     * del project from database
     * @param projectId project id
     */
    void remove(long projectId);

}
