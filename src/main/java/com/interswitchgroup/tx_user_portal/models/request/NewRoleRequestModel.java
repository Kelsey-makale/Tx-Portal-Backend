package com.interswitchgroup.tx_user_portal.models.request;

public class NewRoleRequestModel {
    private String role_name;

    public NewRoleRequestModel() {
    }

    public NewRoleRequestModel(String role_name) {
        this.role_name = role_name;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
