package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

/**
 * the offset between consumer and broker.
 */
public class ConsumeProgress {

    private long id;

    private String consumerGroup;

    private String topic;

    private String brokerName;

    private int queueId;

    private long brokerOffset;

    private long consumerOffset;

    // 消费的最后一条消息对应的时间戳
    private long lastTimestamp;

    private long diff;

    private Date createTime;

    public ConsumeProgress() {
    }

    public long getDiff() {
        return diff;
    }

    public void setDiff(long diff) {
        this.diff = diff;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public long getBrokerOffset() {
        return brokerOffset;
    }

    public void setBrokerOffset(long brokerOffset) {
        this.brokerOffset = brokerOffset;
    }

    public long getConsumerOffset() {
        return consumerOffset;
    }

    public void setConsumerOffset(long consumerOffset) {
        this.consumerOffset = consumerOffset;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "ConsumeProgress{" +
                "id=" + id +
                ", consumerGroup=" + consumerGroup +
                ", topic='" + topic + '\'' +
                ", brokerName='" + brokerName + '\'' +
                ", queueId=" + queueId +
                ", brokerOffset=" + brokerOffset +
                ", consumerOffset=" + consumerOffset +
                ", lastTimestamp=" + lastTimestamp +
                ", diff=" + diff +
                ", createTime=" + createTime +
                '}';
    }
}
