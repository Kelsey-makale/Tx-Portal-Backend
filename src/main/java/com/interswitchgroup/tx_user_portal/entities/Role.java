package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", updatable = false)
    private long role_id;

    @Column(name = "role_name", nullable = false)
    private String role_name;

    public Role() {
    }

    public Role(long role_id, String role_name) {
        this.role_id = role_id;
        this.role_name = role_name;
    }

    public long getRole_id() {
        return role_id;
    }

    public void setRole_id(long role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role_id=" + role_id +
                ", role_name=" + role_name +
                '}';
    }
}
