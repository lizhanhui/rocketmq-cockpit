package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroupHosting;

import java.util.List;

public interface CockpitConsumerGroupDBService {

    List<ConsumerGroup> list(long projectId, String clusterName, String consumerGroupName, long brokerId,
                             String brokerAddress);

    List<ConsumerGroup> listByTopic(long topicId);

    ConsumerGroup get(long consumerGroupId, String consumerGroupName);

    void activate(long consumerGroupId);

    void insert(ConsumerGroup consumerGroup, long projectId);

    void delete(long consumerGroupId);

    void update(ConsumerGroup consumerGroup);

    void refresh(long brokerId, long consumerGroupId);

    List<ConsumerGroupHosting> listEndangeredConsumerGroupsByBroker(long brokerId);
}
