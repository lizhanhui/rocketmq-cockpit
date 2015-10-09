package com.ndpmedia.rocketmq.cockpit.model;

public enum Status {
    NULL(0, "NULL", ""),
    DRAFT(1, "DRAFT", "Draft"),
    PENDING(2, "PENDING", "Pending"),
    APPROVED(3, "APPROVED", "Approved"),
    REJECTED(4, "REJECTED", "Rejected"),
    ACTIVE(5, "ACTIVE", "Active"),
    DELETED(6, "DELETED", "Deleted");

    private int id;

    private String value;

    private String text;

    Status(int id, String value, String text) {
        this.id = id;
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
