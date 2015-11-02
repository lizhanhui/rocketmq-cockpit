package com.ndpmedia.rocketmq.cockpit.model;

/**
 * Created by robert on 2015/9/22.
 */
public class TopicPerSecond {
    private long timeStamp;

    private double tps;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }
}
