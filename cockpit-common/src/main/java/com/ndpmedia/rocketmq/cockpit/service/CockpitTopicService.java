package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.Set;

public interface CockpitTopicService {

    Set<String> fetchTopics();

    boolean createOrUpdateTopic(Topic topic);

    boolean deleteTopic(Topic topic);

    void insert(Topic topic, long teamId);

    void remove(long topicId, long teamId);
}
