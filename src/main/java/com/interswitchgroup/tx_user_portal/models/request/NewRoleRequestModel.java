package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class NewRoleRequestModel {
    private String role_name;
    private List<String> role_rights;

    public NewRoleRequestModel() {
    }

    public NewRoleRequestModel(String role_name, List<String> role_rights) {
        this.role_name = role_name;
        this.role_rights = role_rights;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public List<String> getRole_rights() {
        return role_rights;
    }

    public void setRole_rights(List<String> role_rights) {
        this.role_rights = role_rights;
    }
}
