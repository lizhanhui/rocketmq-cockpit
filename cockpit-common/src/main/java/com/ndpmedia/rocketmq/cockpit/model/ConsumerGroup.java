package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class ConsumerGroup {

    private long id;

    private int whichBrokerWhenConsumeSlowly;

    private String groupName;

    private boolean consumeEnable;

    private boolean consumeBroadcastEnable;

    /**
     * Optional value indicating which broker to create this consumer group as subscription.
     */
    private String brokerAddress;

    /**
     * Consume from which broker.
     */
    private int brokerId;

    private Integer retryMaxTimes = 3;

    private Integer retryQueueNum = 3;

    private boolean consumeFromMinEnable;

    private String clusterName;

    private Status status;

    private Date createTime;

    private Date updateTime = new Date();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWhichBrokerWhenConsumeSlowly() {
        return whichBrokerWhenConsumeSlowly;
    }

    public void setWhichBrokerWhenConsumeSlowly(int whichBrokerWhenConsumeSlowly) {
        this.whichBrokerWhenConsumeSlowly = whichBrokerWhenConsumeSlowly;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isConsumeEnable() {
        return consumeEnable;
    }

    public void setConsumeEnable(boolean consumeEnable) {
        this.consumeEnable = consumeEnable;
    }

    public boolean isConsumeBroadcastEnable() {
        return consumeBroadcastEnable;
    }

    public void setConsumeBroadcastEnable(boolean consumeBroadcastEnable) {
        this.consumeBroadcastEnable = consumeBroadcastEnable;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public Integer getRetryMaxTimes() {
        return retryMaxTimes;
    }

    public void setRetryMaxTimes(Integer retryMaxTimes) {
        this.retryMaxTimes = retryMaxTimes;
    }

    public Integer getRetryQueueNum() {
        return retryQueueNum;
    }

    public void setRetryQueueNum(Integer retryQueueNum) {
        this.retryQueueNum = retryQueueNum;
    }

    public boolean isConsumeFromMinEnable() {
        return consumeFromMinEnable;
    }

    public void setConsumeFromMinEnable(boolean consumeFromMinEnable) {
        this.consumeFromMinEnable = consumeFromMinEnable;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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
}

