package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class ConsumerGroupHosting {

    private ConsumerGroup consumerGroup;

    private Broker broker;

    private Date syncTime;

    public ConsumerGroup getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(ConsumerGroup consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }
}
