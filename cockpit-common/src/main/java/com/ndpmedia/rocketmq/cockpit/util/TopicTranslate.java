package com.ndpmedia.rocketmq.cockpit.util;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.ndpmedia.rocketmq.cockpit.model.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robert on 2015/6/11.
 */
public class TopicTranslate {

    private static final Set<String> DEFAULT_TOPICS = new HashSet<>();

    {
        DEFAULT_TOPICS.add("DefaultCluster");
        DEFAULT_TOPICS.add("DefaultCluster_3_broker1");
        DEFAULT_TOPICS.add("DefaultCluster_3_broker2");
        DEFAULT_TOPICS.add("DefaultCluster_2_broker1");
        DEFAULT_TOPICS.add("DefaultCluster_2_broker2");
        DEFAULT_TOPICS.add("DefaultCluster_1_broker1");
        DEFAULT_TOPICS.add("DefaultCluster_1_broker2");
    }

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

        topicConfig.setPerm(topicBrokerInfo.getPermission());

        topicConfig.setOrder(topicBrokerInfo.getTopicMetadata().isOrder());

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

    public static TopicBrokerInfo createTopicBrokerInfo(Broker broker, TopicMetadata topicMetadata){
        return createTopicBrokerInfo(broker, topicMetadata, TopicConfig.DefaultReadQueueNums, TopicConfig.DefaultWriteQueueNums);
    }

    public static TopicBrokerInfo createTopicBrokerInfo(Broker broker, TopicMetadata topicMetadata, int readQueueNum, int writeQueueNum){
        return createTopicBrokerInfo(broker, topicMetadata, readQueueNum, writeQueueNum, 6);
    }

    public static TopicBrokerInfo createTopicBrokerInfo(Broker broker, TopicMetadata topicMetadata, int readQueueNum,
                                                        int writeQueueNum, int perm){
        TopicBrokerInfo topicBrokerInfo = new TopicBrokerInfo();
        topicBrokerInfo.setBroker(broker);
        topicBrokerInfo.setTopicMetadata(topicMetadata);
        topicBrokerInfo.setCreateTime(new Date());
        topicBrokerInfo.setUpdateTime(new Date());
        topicBrokerInfo.setSyncTime(new Date());
        topicBrokerInfo.setPermission(perm);
        topicBrokerInfo.setReadQueueNum(readQueueNum);
        topicBrokerInfo.setWriteQueueNum(writeQueueNum);
        topicBrokerInfo.setStatus(Status.ACTIVE);
        return topicBrokerInfo;
    }

    public static TopicMetadata createTopicMetadata(String topic, String clusterName){
        TopicMetadata topicMetadata = new TopicMetadata();
        topicMetadata.setTopic(topic);
        topicMetadata.setStatus(Status.ACTIVE);
        topicMetadata.setCreateTime(new Date());
        topicMetadata.setUpdateTime(new Date());
        // TODO decode and set order info here.
        topicMetadata.setClusterName(clusterName);
        return topicMetadata;
    }

    public static boolean isGroup(String topic){
        if (topic.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX)
                || topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX))
            return true;
        return false;
    }

    public static boolean isDefault(String topic){
        if (DEFAULT_TOPICS.contains(topic))
            return true;

        return false;
    }
}
