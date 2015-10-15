package com.ndpmedia.rocketmq.cockpit.model;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private long id;
    private String name;
    private String description;
    private long teamId;

    private List<TopicMetadata> topics = new ArrayList<>();

    private List<ConsumerGroup> consumerGroups = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public List<TopicMetadata> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicMetadata> topics) {
        this.topics = topics;
    }

    public List<ConsumerGroup> getConsumerGroups() {
        return consumerGroups;
    }

    public void setConsumerGroups(List<ConsumerGroup> consumerGroups) {
        this.consumerGroups = consumerGroups;
    }
}
