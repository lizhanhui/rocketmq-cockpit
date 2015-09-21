package com.ndpmedia.rocketmq.cockpit.model;

public class ConsumerGroupHosting {

    private ConsumerGroup consumerGroup;

    private Broker broker;

    private Status status;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
