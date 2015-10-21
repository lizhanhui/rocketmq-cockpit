package com.ndpmedia.rocketmq.cockpit.model;

public enum ResourceType {
    NULL(0, "Undefined"),
    PROJECT(1, "Project");

    private int id;

    private String name;

    ResourceType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
