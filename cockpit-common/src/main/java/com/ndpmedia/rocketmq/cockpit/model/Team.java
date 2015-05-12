package com.ndpmedia.rocketmq.cockpit.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private long id;

    private String name;

    private List<CockpitUser> cockpitUsers = new ArrayList<CockpitUser>();

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

    public List<CockpitUser> getCockpitUsers() {
        return cockpitUsers;
    }

    public void setCockpitUsers(List<CockpitUser> cockpitUsers) {
        this.cockpitUsers = cockpitUsers;
    }
}
