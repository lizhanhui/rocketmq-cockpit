package com.ndpmedia.rocketmq.cockpit.model;

public class TopicAvailability {

    private long topicId;

    private int dcId;

    /**
     * Number of brokers that have this topic.
     */
    private int availability;

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public int getDcId() {
        return dcId;
    }

    public void setDcId(int dcId) {
        this.dcId = dcId;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
}
