package com.interswitchgroup.tx_user_portal.models;

public class CreatedUser {
    private String username;
    private long organization_id;

    public CreatedUser(String username, long organization_id) {
        this.username = username;
        this.organization_id = organization_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(long organization_id) {
        this.organization_id = organization_id;
    }
}
