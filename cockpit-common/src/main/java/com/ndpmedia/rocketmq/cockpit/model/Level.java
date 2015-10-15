package com.ndpmedia.rocketmq.cockpit.model;

public enum Level {
    NULL(0, "Null", "null"),
    INFO(1, "Info", "info"),
    WARN(2, "Warn", "warn"),
    ERROR(3, "Error", "error"),
    CRITICAL(4, "Critical", "critical"),
    FATAL(5, "Fatal", "fatal");

    private int id;

    private String name;

    private String info;

    Level(int id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }
}
