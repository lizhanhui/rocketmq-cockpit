package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

/**
 * topic bean.
 */
public class Topic {

    public static final int DEFAULT_READ_QUEUE_NUM = 16;

    public static final int DEFAULT_WRITE_QUEUE_NUM = 16;

    private long id;

    private String topic;

    private String clusterName;

    private String brokerAddress;

    private int writeQueueNum = DEFAULT_WRITE_QUEUE_NUM;

    private int readQueueNum = DEFAULT_READ_QUEUE_NUM;

    //READ and WRITE
    private int permission = 6;

    private boolean unit;

    private boolean hasUnitSubscription;

    private boolean order;

    private Status status = Status.DRAFT;

    private Date createTime;

    private Date updateTime = new Date();

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
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

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public boolean isUnit() {
        return unit;
    }

    public void setUnit(boolean unit) {
        this.unit = unit;
    }

    public boolean isHasUnitSubscription() {
        return hasUnitSubscription;
    }

    public void setHasUnitSubscription(boolean hasUnitSubscription) {
        this.hasUnitSubscription = hasUnitSubscription;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", brokerAddress='" + brokerAddress + '\'' +
                ", writeQueueNum=" + writeQueueNum +
                ", readQueueNum=" + readQueueNum +
                ", permission=" + permission +
                ", unit=" + unit +
                ", hasUnitSubscription=" + hasUnitSubscription +
                ", order=" + order +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
