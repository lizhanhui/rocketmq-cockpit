package com.ndpmedia.rocketmq.cockpit.model;

public class KV implements Comparable {

    private long id;

    private String nameSpace;

    private String key;

    private String value;

    private Status status = Status.DRAFT;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof KV) {
            KV that = (KV) o;
            if (0 == this.nameSpace.compareTo(that.getNameSpace())) {
                return this.key.compareTo(that.getKey());
            } else {
                return this.nameSpace.compareTo(that.getNameSpace());
            }
        }

        return 0;
    }
}
