package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;

public interface CockpitConsumerGroupService {

    void insert(ConsumerGroup consumerGroup, long teamId);

    void delete(long consumerGroupId);

    ConsumerGroup getBaseBean(String consumerGroupName);

}
