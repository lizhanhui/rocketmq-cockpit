package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.subscription.SubscriptionGroupConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;

import java.util.Set;

public interface CockpitConsumerGroupService {

    ConsumerGroup get(long consumerGroupId, String consumerGroupName);

    void activate(long consumerGroupId);

    boolean update(ConsumerGroup consumerGroup);

    boolean clear(ConsumerGroup consumerGroup);

    void insert(ConsumerGroup consumerGroup, long projectId);

    void delete(long consumerGroupId);

    Set<String> getGroups(DefaultMQAdminExt defaultMQAdminExt);

    SubscriptionGroupConfig getGroupConfig(DefaultMQAdminExt defaultMQAdminExt, String groupName);
}
