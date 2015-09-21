package com.ndpmedia.rocketmq.cockpit.model;

public class ConsumerGroupHosting {

    private ConsumerGroup consumerGroup;

    private Broker broker;

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
}
