package com.ndpmedia.rocketmq.cockpit.model;

public enum Status {

    NULL(0, "NULL"),
    DRAFT(1, "DRAFT"),
    ACTIVE(2, "ACTIVE"),
    DELETED(3, "DELETED");

    private String value;

    private int id;

    Status(int id, String value) {
        this.value = value;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }
}
