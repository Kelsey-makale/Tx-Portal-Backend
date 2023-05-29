package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class NewOrganizationRequestModel {
    private String organization_name;
    private List<RoleData> roles;

    public NewOrganizationRequestModel() {
    }

    public NewOrganizationRequestModel(String organization_name, List<RoleData> roles) {
        this.organization_name = organization_name;
        this.roles = roles;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public List<RoleData> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleData> roles) {
        this.roles = roles;
    }

    public static class RoleData{
        private Long roleId;
        private List<Long> rightIds;

        public RoleData() {
        }

        public RoleData(Long roleId, List<Long> rightIds) {
            this.roleId = roleId;
            this.rightIds = rightIds;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public List<Long> getRightIds() {
            return rightIds;
        }

        public void setRightIds(List<Long> rightIds) {
            this.rightIds = rightIds;
        }
    }
}


