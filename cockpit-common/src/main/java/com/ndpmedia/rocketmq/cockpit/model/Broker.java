package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class Broker {
    private long id;

    private String clusterName;

    private String brokerName;

    private int brokerId;

    private String address;

    private String version;

    private int dc;

    private Date lastUpdateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getDc() {
        return dc;
    }

    public void setDc(int dc) {
        this.dc = dc;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Broker broker = (Broker) o;

        return id == broker.getId() ||
                       (brokerId == broker.brokerId &&
                        clusterName.equals(broker.clusterName) &&
                        brokerName.equals(broker.brokerName));
    }

    @Override
    public int hashCode() {
        int result = clusterName.hashCode();
        result = 31 * result + brokerName.hashCode();
        result = 31 * result + brokerId;
        return result;
    }
}
