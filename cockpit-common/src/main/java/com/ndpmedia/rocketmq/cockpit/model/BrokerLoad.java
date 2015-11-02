package com.ndpmedia.rocketmq.cockpit.model;

public class BrokerLoad {

    private long brokerId;

    private int dcId;

    private int readQueueNum;

    private int writeQueueNum;

    private float inTPS;

    private float outTPS;

    public long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(long brokerId) {
        this.brokerId = brokerId;
    }

    public int getDcId() {
        return dcId;
    }

    public void setDcId(int dcId) {
        this.dcId = dcId;
    }

    public int getReadQueueNum() {
        return readQueueNum;
    }

    public void setReadQueueNum(int readQueueNum) {
        this.readQueueNum = readQueueNum;
    }

    public int getWriteQueueNum() {
        return writeQueueNum;
    }

    public void setWriteQueueNum(int writeQueueNum) {
        this.writeQueueNum = writeQueueNum;
    }

    public float getInTPS() {
        return inTPS;
    }

    public void setInTPS(float inTPS) {
        this.inTPS = inTPS;
    }

    public float getOutTPS() {
        return outTPS;
    }

    public void setOutTPS(float outTPS) {
        this.outTPS = outTPS;
    }
}
