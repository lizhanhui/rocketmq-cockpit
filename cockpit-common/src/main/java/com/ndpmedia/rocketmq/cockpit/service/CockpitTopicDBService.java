package com.ndpmedia.rocketmq.cockpit.service;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.List;

public interface CockpitTopicDBService extends CockpitTopicBaseService {

    List<Topic> getTopics(Status status);

    /**
     * get topic list by topic name from database
     * @param topic topic name
     * @return topic list
     */
    Topic getTopic(String topic);

    /**
     * update topic status to active on database
     * @param id the topic id on database
     * @return result
     */
    boolean activate(long id);

    /**
     * update topic status to del on database
     * @param id the topic id on database
     * @return result
     */
    boolean deactivate(long id);


    /**
     * add topic to database by project
     * @param topic topic
     * @param projectId ID of the project this topic will be added to.
     */
    void insert(Topic topic, long projectId);

    /**
     * delete topic and remove topic-project relationship from database
     * @param topicId Topic id
     * @param projectId Project id
     */
    void remove(long topicId, long projectId);

    boolean exists(String topic);

    List<Long> getProjectIDs(long topicId, String topic);
}