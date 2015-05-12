package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

/**
 * Created by robert.xu on 2015/4/22.
 * 消息流 环节实例
 */
public class CockpitMessageFlow {

    private String id;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 消息标签
     */
    private String tags;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息流识别ID
     */
    private String tracerId;

    /**
     * 消息流当前环节标识
     */
    private String source;

    /**
     * 消息流当前环节状态
     */
    private String status;

    /**
     * 消息流源
     */
    private String producerGroup;

    private String consumerGroup;

    private String bornHost;

    private String broker;

    private String client;

    private String messageQueue;

    private String offSet;

    private String ipFrom;

    private String ipTo;

    private Date createTime;

    private long timeStamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTracerId() {
        return tracerId;
    }

    public void setTracerId(String tracerId) {
        this.tracerId = tracerId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getBornHost() {
        return bornHost;
    }

    public void setBornHost(String bornHost) {
        this.bornHost = bornHost;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(String messageQueue) {
        this.messageQueue = messageQueue;
    }

    public String getOffSet() {
        return offSet;
    }

    public void setOffSet(String offSet) {
        this.offSet = offSet;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getIpFrom() {
        return ipFrom;
    }

    public void setIpFrom(String ipFrom) {
        this.ipFrom = ipFrom;
    }

    public String getIpTo() {
        return ipTo;
    }

    public void setIpTo(String ipTo) {
        this.ipTo = ipTo;
    }
}
