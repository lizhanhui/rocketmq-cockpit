package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.MQAdminExt;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;

import java.util.Set;

public interface CockpitTopicMQService extends CockpitTopicBaseService {

    Set<String> fetchAllTopics(MQAdminExt adminExt, boolean includeSystemTopic) throws CockpitException;


    /**
     * get topic config by topic name from name server
     * @param adminExt connect to name server
     * @param topic topic name
     * @return topic config
     */
    TopicConfig getTopicConfigByTopicName(MQAdminExt adminExt, String topic) throws CockpitException;

    /**
     * get brokers from name server by topic route
     * @param adminExt connect to name server
     * @param topic topic name
     * @param masterOnly Indicate if we only fetch master broker addresses.
     * @return broker addresses.
     */
    Set<String> getTopicBrokers(MQAdminExt adminExt, String topic, boolean masterOnly) throws CockpitException;

    /**
     * try to update or create topic config on broker
     * @param adminExt connect to name server
     * @param topicConfig topic config
     * @param broker broker address
     * @return result
     */
    boolean rebuildTopicConfig(MQAdminExt adminExt, TopicConfig topicConfig, String broker) throws CockpitException;

    /**
     * create or update topic on cluster or broker
     * @param topicMetadata topic
     * @return result
     */
    boolean createOrUpdateTopic(MQAdminExt adminExt, TopicMetadata topicMetadata) throws CockpitException;

    boolean createOrUpdateTopic(MQAdminExt adminExt, TopicBrokerInfo topicBrokerInfo) throws CockpitException;

    /**
     * delete topic on cluster or broker
     * @param topicMetadata topic
     * @return result
     */
    boolean deleteTopic(DefaultMQAdminExt adminExt, TopicMetadata topicMetadata) throws CockpitException;

    boolean deleteTopicByBroker(DefaultMQAdminExt adminExt, TopicBrokerInfo topicBrokerInfo) throws CockpitException;

}
