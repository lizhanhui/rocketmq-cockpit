package com.ndpmedia.rocketmq.cockpit.model;

/**
 * Created by macbookpro on 15/10/13.
 */
public class Chair {

    private Desk desk;

    private long id;

    private String name;

    public Desk getDesk() {
        return desk;
    }

    public void setDesk(Desk desk) {
        this.desk = desk;
    }

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
}
