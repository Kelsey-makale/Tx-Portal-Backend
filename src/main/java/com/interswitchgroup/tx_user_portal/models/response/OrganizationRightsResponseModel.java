package com.interswitchgroup.tx_user_portal.models.response;

import java.util.List;

public class OrganizationRightsResponseModel {
    private Long organizationId;
    private String organizationName;
    private List<RoleDataModel> roles;

    public OrganizationRightsResponseModel() {
    }

    public OrganizationRightsResponseModel(Long organizationId, String organizationName, List<RoleDataModel> roles) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.roles = roles;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<RoleDataModel> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDataModel> roles) {
        this.roles = roles;
    }

    public static class RoleDataModel{
        private Long roleId;
        private String roleName;
        private List<RightDataModel> rights;

        public RoleDataModel() {
        }

        public RoleDataModel(Long roleId, String roleName, List<RightDataModel> rights) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.rights = rights;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public List<RightDataModel> getRights() {
            return rights;
        }

        public void setRights(List<RightDataModel> rights) {
            this.rights = rights;
        }
    }


    public static class RightDataModel{
        private Long rightId;
        private String rightName;

        public RightDataModel() {
        }

        public RightDataModel(Long rightId, String rightName) {
            this.rightId = rightId;
            this.rightName = rightName;
        }

        public Long getRightId() {
            return rightId;
        }

        public void setRightId(Long rightId) {
            this.rightId = rightId;
        }

        public String getRightName() {
            return rightName;
        }

        public void setRightName(String rightName) {
            this.rightName = rightName;
        }
    }
}

