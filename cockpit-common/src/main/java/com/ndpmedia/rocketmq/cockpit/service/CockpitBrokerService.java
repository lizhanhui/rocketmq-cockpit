package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;

import java.util.Map;
import java.util.Set;

/**
 * Created by robert on 2015/6/9.
 */
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

    boolean removeAllTopic(String broker);
}
