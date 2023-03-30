package com.interswitchgroup.tx_user_portal.models.request;

public class NewOrganizationRequestModel {
    private String organization_name;

    public NewOrganizationRequestModel() {
    }

    public NewOrganizationRequestModel(String organization_name) {
        this.organization_name = organization_name;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }
}
