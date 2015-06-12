package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.List;
import java.util.Set;

public interface CockpitTopicService {

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
     * get status is del topic list from database
     * @return  topic list
     */
    List<Topic> getDelTopics();

    /**
     * get active topic list from database
     * @return topic list
     */
    List<Topic> getActiveTopics();

    /**
     * get topic list by topic name from database
     * @param topic topic name
     * @return topic list
     */
    List<Topic> getTopic(String topic);

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

    /**
     * update topic status to active on database
     * @param id the topic id on database
     * @return result
     */
    boolean register(long id);

    /**
     * update topic status to del on database
     * @param id the topic id on database
     * @return result
     */
    boolean unregister(long id);

    /**
     * get the team id list which team had this topic
     * @param topic topic
     * @return team id list
     */
    List<Long> getTeamId(Topic topic);

    /**
     * add topic to database by team
     * @param topic topic
     * @param teamId which team add this topic
     */
    void insert(Topic topic, long teamId);

    /**
     * del topic team relationship from database
     * @param topicId topic id
     * @param teamId team id
     */
    void remove(long topicId, long teamId);


}
