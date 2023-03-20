package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id", updatable = false)
    private long organization_id;

    @Column(name = "org_name", nullable = false)
    private String organization_name;

    public Organization() {
    }

    public Organization(long organization_id, String organization_name) {
        this.organization_id = organization_id;
        this.organization_name = organization_name;
    }

    public long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(long organization_id) {
        this.organization_id = organization_id;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organization_id=" + organization_id +
                ", organization_name='" + organization_name + '\'' +
                '}';
    }
}

