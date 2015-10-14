package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;

import java.util.List;

public interface CockpitProjectService {

    List<Project> list(long teamId);

    /**
     * add project to database by team
     * @param project project
     */
    void insert(Project project);

    void addTopic(long projectId, long topicId);

    void addConsumerGroup(long projectId, long consumerGroupId);

    /**
     * del project from database
     * @param projectId project id
     */
    void remove(long projectId);

    /**
     * get project by id or name
     * @param projectId id
     * @return  project
     */
    Project get(long projectId);

    List<ConsumerGroup> getConsumerGroups(long projectId);

    List<TopicMetadata> getTopics(long projectId);

}
