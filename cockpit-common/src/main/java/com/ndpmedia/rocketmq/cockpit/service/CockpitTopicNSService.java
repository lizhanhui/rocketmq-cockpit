package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.List;
import java.util.Set;

public interface CockpitTopicNSService {

    /**
     * get topic list include CG retry topic
     * @return topic list
     */
    Set<String> fetchTopics();

    /**
     * get topic list without CG retry topic from name server
     * @param defaultMQAdminExt connect to name server
     * @return topic list
     */
    Set<String> getTopics(DefaultMQAdminExt defaultMQAdminExt);

    /**
     * get topic config by topic name from name server
     * @param defaultMQAdminExt connect to name server
     * @param topic topic name
     * @return topic config
     */
    TopicConfig getTopicConfigByTopicName(DefaultMQAdminExt defaultMQAdminExt, String topic);

    /**
     * get brokers from name server by topic route
     * @param defaultMQAdminExt connect to name server
     * @param topic topic name
     * @return brokers
     */
    Set<String> getTopicBrokers(DefaultMQAdminExt defaultMQAdminExt, String topic);

    /**
     * try to update or create topic config on broker
     * @param defaultMQAdminExt connect to name server
     * @param topicConfig topic config
     * @param broker broker address
     * @return result
     */
    boolean rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig, String broker);

    /**
     * create or update topic on cluster or broker
     * @param topic topic
     * @return result
     */
    boolean createOrUpdateTopic(Topic topic);

    /**
     * del topic on cluster or broker
     * @param topic topic
     * @return result
     */
    boolean deleteTopic(Topic topic);

}
