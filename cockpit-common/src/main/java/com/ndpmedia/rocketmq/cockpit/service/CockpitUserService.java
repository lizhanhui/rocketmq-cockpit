package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.CockpitUser;
import com.ndpmedia.rocketmq.cockpit.model.ResourceType;

public interface CockpitUserService {
    void registerUser(CockpitUser cockpitUser);

    boolean hasAccess(long teamId, long resourceId, ResourceType resourceType);
}
