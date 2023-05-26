package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.util.List;
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

    private List<Role> roles;

    @ManyToMany
    @JoinTable(name = "organization_right",
            joinColumns = @JoinColumn(name = "organizationId"),
            inverseJoinColumns = @JoinColumn(name = "right_id")
    )
    private List<Right> rights;


    public Organization() {
    }

    public Organization(long organizationId, String organizationName) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
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

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                '}';
    }
}

