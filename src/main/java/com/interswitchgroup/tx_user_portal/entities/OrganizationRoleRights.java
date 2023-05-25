package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;


@Entity
@IdClass(OrganizationRoleRightsId.class)
public class OrganizationRoleRights {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "right_id", nullable = false)
    private Right right;

    public OrganizationRoleRights() {
    }

    public OrganizationRoleRights(Organization organization, Role role, Right right) {
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
}




