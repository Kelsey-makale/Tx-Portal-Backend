package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class NewOrganizationRequestModel {
    private String organization_name;
    private List<Long> roleIds;

    public NewOrganizationRequestModel() {
    }

    public NewOrganizationRequestModel(String organization_name, List<Long> roleIds) {
        this.organization_name = organization_name;
        this.roleIds = roleIds;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
