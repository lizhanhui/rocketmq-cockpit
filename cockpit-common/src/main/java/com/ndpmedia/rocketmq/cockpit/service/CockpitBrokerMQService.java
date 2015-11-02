package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;

import java.util.Map;
import java.util.Set;

/**
 * Created by macbookpro on 15/10/10.
 */
public interface CockpitBrokerMQService {
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
}
