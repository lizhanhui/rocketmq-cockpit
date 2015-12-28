package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;

import java.util.List;

public interface CockpitConsumeProgressService {
    List<ConsumeProgress> queryConsumerProgress(String groupName, String topic, String broker);
    List<ConsumeProgress> queryConsumerProgress(DefaultMQAdminExt adminExt, String groupName, String topic, String broker);

}
