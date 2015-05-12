package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class IPPair {

    private long id;

    private String innerIP;

    private String publicIP;

    private Date createTime = new Date();

    private Date updateTime = new Date();

    public IPPair() {
    }

    public IPPair(String innerIP, String publicIP) {
        this.innerIP = innerIP;
        this.publicIP = publicIP;
    }

    public String getInnerIP() {
        return innerIP;
    }

    public void setInnerIP(String innerIP) {
        this.innerIP = innerIP;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
