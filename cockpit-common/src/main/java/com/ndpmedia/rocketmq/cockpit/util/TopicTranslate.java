package com.ndpmedia.rocketmq.cockpit.util;

import com.alibaba.rocketmq.common.TopicConfig;
import com.ndpmedia.rocketmq.cockpit.model.Topic;

/**
 * Created by robert on 2015/6/11.
 */
public class TopicTranslate {

    public static TopicConfig translateFrom(Topic topic){
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topic.getTopic());
        topicConfig.setWriteQueueNums(topic.getWriteQueueNum());
        topicConfig.setReadQueueNums(topic.getReadQueueNum());
        topicConfig.setPerm(topic.getPermission());

        return topicConfig;
    }
}
