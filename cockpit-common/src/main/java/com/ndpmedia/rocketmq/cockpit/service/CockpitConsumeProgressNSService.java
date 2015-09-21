package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;

import java.util.List;

public interface CockpitConsumeProgressNSService {
    List<ConsumeProgress> queryConsumerProgress(String groupName, String topic, String broker);

    List<ConsumeProgress> queryConsumerProgress(DefaultMQAdminExt defaultMQAdminExtm, String groupName, String topic, String broker);

}
