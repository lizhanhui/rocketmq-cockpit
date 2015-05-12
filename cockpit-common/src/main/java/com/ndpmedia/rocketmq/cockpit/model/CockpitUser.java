package com.ndpmedia.rocketmq.cockpit.model;

import java.util.ArrayList;
import java.util.List;

public class CockpitUser {

    private long id;

    private String username;

    private String password;

    private Team team;

    private String email;

    private Status status = Status.DRAFT;

    private List<CockpitRole> cockpitRoles = new ArrayList<CockpitRole>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CockpitRole> getCockpitRoles() {
        return cockpitRoles;
    }

    public void setCockpitRoles(List<CockpitRole> cockpitRoles) {
        this.cockpitRoles = cockpitRoles;
    }
}
