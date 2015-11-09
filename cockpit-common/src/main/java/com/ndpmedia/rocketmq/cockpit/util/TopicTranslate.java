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


    public static TopicConfig wrapTopicToTopicConfig(TopicBrokerInfo topicBrokerInfo) {
        TopicConfig topicConfig = new TopicConfig();

        topicConfig.setWriteQueueNums(topicBrokerInfo.getWriteQueueNum());
        if (topicConfig.getWriteQueueNums() <= 0) {
            topicConfig.setWriteQueueNums(TopicConfig.DefaultWriteQueueNums);
        }

        topicConfig.setReadQueueNums(topicBrokerInfo.getReadQueueNum());
        if (topicConfig.getReadQueueNums() <= 0) {
            topicConfig.setReadQueueNums(TopicConfig.DefaultReadQueueNums);
        }

        topicConfig.setTopicName(topicBrokerInfo.getTopicMetadata().getTopic());

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
