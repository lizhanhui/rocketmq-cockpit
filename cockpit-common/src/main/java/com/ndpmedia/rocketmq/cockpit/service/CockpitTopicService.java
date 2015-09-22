package com.ndpmedia.rocketmq.cockpit.service;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.List;
import java.util.Set;

public interface CockpitTopicService {

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

    Topic getBaseBean(String topicName);
}
