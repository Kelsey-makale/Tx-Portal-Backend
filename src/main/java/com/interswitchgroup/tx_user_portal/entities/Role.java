package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
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

    @ManyToMany
    @JoinTable(name = "role_right", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    }, inverseJoinColumns = {@JoinColumn(name = "right_id", referencedColumnName = "right_id")})
    private List<Right> rights = new ArrayList<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Request> requests;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Organization> organizations;

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

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }
}
