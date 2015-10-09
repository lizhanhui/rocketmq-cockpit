package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;

public interface CockpitConsumerGroupDBService {

    ConsumerGroup get(long consumerGroupId, String consumerGroupName);

    void activate(long consumerGroupId);

    void insert(ConsumerGroup consumerGroup, long projectId);

    void delete(long consumerGroupId);

    void refresh(long brokerId, long consumerGroupId);
}
