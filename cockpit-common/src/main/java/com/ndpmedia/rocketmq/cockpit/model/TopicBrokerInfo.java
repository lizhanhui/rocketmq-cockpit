package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class TopicBrokerInfo {

    private TopicMetadata topicMetadata;

    private Broker broker;

    private int permission;

    private int writeQueueNum;

    private int readQueueNum;

    private Status status;

    private Date createTime;

    private Date updateTime;

    private Date syncTime;

    public TopicMetadata getTopicMetadata() {
        return topicMetadata;
    }

    public void setTopicMetadata(TopicMetadata topicMetadata) {
        this.topicMetadata = topicMetadata;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getWriteQueueNum() {
        return writeQueueNum;
    }

    public void setWriteQueueNum(int writeQueueNum) {
        this.writeQueueNum = writeQueueNum;
    }

    public int getReadQueueNum() {
        return readQueueNum;
    }

    public void setReadQueueNum(int readQueueNum) {
        this.readQueueNum = readQueueNum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }
}
