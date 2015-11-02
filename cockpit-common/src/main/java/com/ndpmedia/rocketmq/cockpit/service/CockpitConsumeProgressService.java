package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;

import java.util.List;

public interface CockpitConsumeProgressService {
    List<ConsumeProgress> queryConsumerProgress(String groupName, String topic, String broker);

}
