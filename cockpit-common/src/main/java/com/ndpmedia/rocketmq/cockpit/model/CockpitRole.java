package com.ndpmedia.rocketmq.cockpit.model;

public class CockpitRole {

    public static final CockpitRole ROLE_ADMIN = new CockpitRole(1, "ROLE_ADMIN");
    public static final CockpitRole ROLE_MANAGER = new CockpitRole(2, "ROLE_MANAGER");
    public static final CockpitRole ROLE_USER = new CockpitRole(3, "ROLE_USER");
    public static final CockpitRole ROLE_WATCHER = new CockpitRole(4, "ROLE_WATCHER");

    private long id;

    private String name;

    public CockpitRole() {

    }

    public CockpitRole(long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CockpitRole)) return false;

        CockpitRole that = (CockpitRole) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }
}
