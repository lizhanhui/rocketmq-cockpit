package com.ndpmedia.rocketmq.cockpit.util;

import com.alibaba.rocketmq.common.TopicConfig;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

import java.util.Date;

/**
 * Created by robert on 2015/6/11.
 */
public class TopicTranslate {

    /**
     * get topic config by topic
     * @param topic topic
     * @return topic config
     */
    public static TopicConfig wrap(Topic topic){
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topic.getTopic());
        topicConfig.setWriteQueueNums(topic.getWriteQueueNum());
        topicConfig.setReadQueueNums(topic.getReadQueueNum());
        topicConfig.setPerm(topic.getPermission());

        return topicConfig;
    }

    /**
     * get topic by topic config, broker cluster
     * @param topicConfig topic config
     * @param cluster cluster
     * @param broker broker
     * @return topic
     */
    public static Topic wrap(TopicConfig topicConfig, String cluster, String broker){
        Topic topic = new Topic();
        topic.setBrokerAddress(broker);
        topic.setClusterName(cluster);
        topic.setCreateTime(new Date());
        topic.setOrder(topicConfig.isOrder());
        topic.setPermission(topicConfig.getPerm());
        topic.setReadQueueNum(topicConfig.getReadQueueNums());
        topic.setWriteQueueNum(topicConfig.getWriteQueueNums());
        topic.setStatus(Status.ACTIVE);
        topic.setTopic(topicConfig.getTopicName());
        topic.setUpdateTime(new Date());

        return topic;
    }
}
