package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;

import java.util.List;

public interface CockpitConsumerGroupDBService {

    List<ConsumerGroup> listByTopic(long topicId);

    ConsumerGroup get(long consumerGroupId, String consumerGroupName);

    void activate(long consumerGroupId);

    void insert(ConsumerGroup consumerGroup, long projectId);

    void delete(long consumerGroupId);

    void refresh(long brokerId, long consumerGroupId);
}
