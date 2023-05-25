package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id", updatable = false)
    private long organizationId;

    @Column(name = "org_name", nullable = false)
    private String organizationName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "OrganizationRoles", joinColumns = {
            @JoinColumn(name = "organizationId", referencedColumnName = "org_id")
    }, inverseJoinColumns = {@JoinColumn(name = "roleId", referencedColumnName = "role_id")})

    private Set<Role> roles;

    public Organization() {
    }

    public Organization(long organizationId, String organizationName) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public long getOrganization_id() {
        return organizationId;
    }

    public void setOrganization_id(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganization_name() {
        return organizationName;
    }

    public void setOrganization_name(String organizationName) {
        this.organizationName = organizationName;
    }


    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                '}';
    }
}

