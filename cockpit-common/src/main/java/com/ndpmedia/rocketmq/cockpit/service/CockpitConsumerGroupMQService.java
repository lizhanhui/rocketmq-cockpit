package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.subscription.SubscriptionGroupConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;

import java.util.Set;

/**
 * Created by lizhanhui on 15-10-9.
 */
public interface CockpitConsumerGroupMQService {

    boolean update(ConsumerGroup consumerGroup);

    boolean clear(ConsumerGroup consumerGroup);

    Set<String> getGroups(DefaultMQAdminExt defaultMQAdminExt);

    SubscriptionGroupConfig getGroupConfig(DefaultMQAdminExt defaultMQAdminExt, String groupName);
}
