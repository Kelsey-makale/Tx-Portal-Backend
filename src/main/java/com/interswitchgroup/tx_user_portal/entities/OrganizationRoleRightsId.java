package com.interswitchgroup.tx_user_portal.entities;

import java.io.Serializable;
import java.util.Objects;

public class OrganizationRoleRightsId implements Serializable {
    private Organization organization;
    private Role role;
    private Right right;

    public OrganizationRoleRightsId() {
    }

    public OrganizationRoleRightsId(Organization organization, Role role, Right right) {
        this.organization = organization;
        this.role = role;
        this.right = right;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Right getRight() {
        return right;
    }

    public void setRight(Right right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizationRoleRightsId that)) return false;
        return Objects.equals(organization, that.organization) && Objects.equals(role, that.role) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organization, role, right);
    }
}
