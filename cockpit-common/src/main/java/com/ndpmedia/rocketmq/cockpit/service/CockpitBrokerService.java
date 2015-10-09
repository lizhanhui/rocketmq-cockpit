package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;

import java.util.Map;
import java.util.Set;

public interface CockpitBrokerService {

    /**
     * try to get broker list
     * @param defaultMQAdminExt
     * @return
     */
    Set<String> getALLBrokers(DefaultMQAdminExt defaultMQAdminExt);

    /**
     * try to get broker list and which cluster the broker belong to
     * @param defaultMQAdminExt
     * @return
     */
    Map<String, String> getBrokerCluster(DefaultMQAdminExt defaultMQAdminExt);

    /**
     * get cluster names , get broker names;
     * @param defaultMQAdminExt
     * @return
     */
    Set<String> getAllNames(DefaultMQAdminExt defaultMQAdminExt);

    boolean removeAllTopic(String broker);

    Broker get(long brokerId, String brokerAddress);

    boolean hasConsumerGroup(long brokerId, long consumerGroupId);


    void createConsumerGroup(long brokerId, long consumerGroupId);
}
