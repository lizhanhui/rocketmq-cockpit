package com.ndpmedia.rocketmq.cockpit.service;


import com.ndpmedia.rocketmq.cockpit.model.Broker;

import java.util.List;

public interface CockpitBrokerDBService {

    Broker get(long brokerId, String brokerAddress);

    boolean hasConsumerGroup(long brokerId, long consumerGroupId);

    boolean hasTopic(long brokerId, long topicId);

    List<Broker> list(String clusterName, String brokerName, int brokerId, int dc);

    void createConsumerGroup(long brokerId, long consumerGroupId);
}
