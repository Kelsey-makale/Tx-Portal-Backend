package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id", updatable = false)
    private long organizationId;

    @Column(name = "org_name", nullable = false)
    private String organizationName;

    public Organization() {
    }

    public Organization(long organizationId, String organizationName) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
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

