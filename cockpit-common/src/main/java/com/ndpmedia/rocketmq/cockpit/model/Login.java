package com.ndpmedia.rocketmq.cockpit.model;

import java.util.Date;

public class Login {

    private long id;

    private Date loginTime;

    private CockpitUser cockpitUser;

    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public CockpitUser getCockpitUser() {
        return cockpitUser;
    }

    public void setCockpitUser(CockpitUser cockpitUser) {
        this.cockpitUser = cockpitUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
