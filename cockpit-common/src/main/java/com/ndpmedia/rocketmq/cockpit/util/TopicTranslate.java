package com.ndpmedia.rocketmq.cockpit.util;

import com.alibaba.rocketmq.common.TopicConfig;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;

import java.util.Date;

/**
 * Created by robert on 2015/6/11.
 */
public class TopicTranslate {

    /**
     * get topic config by topic
     * @param topicBrokerInfo topic
     * @return topic config
     */
    public static TopicConfig translateFrom(TopicBrokerInfo topicBrokerInfo){
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topicBrokerInfo.getTopicMetadata().getTopic());
        topicConfig.setWriteQueueNums(topicBrokerInfo.getWriteQueueNum());
        topicConfig.setReadQueueNums(topicBrokerInfo.getReadQueueNum());
        topicConfig.setPerm(topicBrokerInfo.getPermission());

        return topicConfig;
    }

    /**
     * get topic by topic config, broker cluster
     * @param topicConfig topic config
     * @param cluster cluster
     * @param broker broker
     * @return topic
     */
    public static Topic translateFrom(TopicConfig topicConfig, String cluster, String broker){
        Topic topic = new Topic();
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
