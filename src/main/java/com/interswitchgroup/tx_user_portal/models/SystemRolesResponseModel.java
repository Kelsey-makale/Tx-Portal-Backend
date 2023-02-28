package com.interswitchgroup.tx_user_portal.models;

import java.util.List;

public class SystemRolesResponseModel {
    private String status;
    private String message;
    private List<Organization> data;

    public SystemRolesResponseModel(String status, String message, List<Organization> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public class Organization{
        private Integer organizationId;
        private String organizationName;
        private List<System> systems;

        public Organization(Integer organizationId, String organizationName, List<System> systems) {
            this.organizationId = organizationId;
            this.organizationName = organizationName;
            this.systems = systems;
        }
    }

    public class System{
        private Integer systemId;
        private String systemName;
        private List<Role> roles;

        public System(Integer systemId, String systemName, List<Role> roles) {
            this.systemId = systemId;
            this.systemName = systemName;
            this.roles = roles;
        }
    }

    public class Role{
        private Integer roleId;
        private String roleName;

        public Role(Integer roleId, String roleName) {
            this.roleId = roleId;
            this.roleName = roleName;
        }
    }
}
