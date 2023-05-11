package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", updatable = false)
    private long role_id;

    @Column(name = "role_name", nullable = false)
    private String role_name;

    @Column(name = "role_description", nullable = false)
    private String role_description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Request> requests;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;

    public Role() {
    }

    public Role(long role_id, String role_name, String role_description) {
        this.role_id = role_id;
        this.role_name = role_name;
        this.role_description = role_description;
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

    public String getRole_description() {
        return role_description;
    }

    public void setRole_description(String role_description) {
        this.role_description = role_description;
    }


}
