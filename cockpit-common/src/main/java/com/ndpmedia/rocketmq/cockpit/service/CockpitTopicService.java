package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.List;
import java.util.Set;

public interface CockpitTopicService {

    /**
     * get topic list include CG retry topic
     * @return
     */
    Set<String> fetchTopics();

    /**
     * get topic list without CG retry topic
     * @param defaultMQAdminExt
     * @return
     */
    Set<String> getTopics(DefaultMQAdminExt defaultMQAdminExt);

    List<Topic> getDelTopics();

    List<Topic> getActiveTopics();

    List<Topic> getTopic(String topic);
    /**
     * get topic config by topic name
     * @param defaultMQAdminExt
     * @param topic
     * @return
     */
    TopicConfig getTopicConfigByTopicName(DefaultMQAdminExt defaultMQAdminExt, String topic);

    /**
     * get topic broker list
     * @param defaultMQAdminExt
     * @param topic
     * @return
     */
    Set<String> getTopicBrokers(DefaultMQAdminExt defaultMQAdminExt, String topic);

    boolean rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig, String broker);

    boolean createOrUpdateTopic(Topic topic);

    boolean deleteTopic(Topic topic);

    boolean register(long id);

    boolean unregister(long id);

    Set<Long> getTeamId(Topic topic);

    void insert(Topic topic, long teamId);

    void remove(long topicId, long teamId);


}
