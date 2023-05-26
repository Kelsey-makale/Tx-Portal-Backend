package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Rights")
public class Right {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long right_id;

    @Column(name = "right_name", nullable = false)
    private String right_name;

    @Column(name = "right_description", nullable = false)
    private String right_description;

    @ManyToMany(mappedBy = "rights")
    private List<Role> roles;

    @ManyToMany(mappedBy = "rights", fetch = FetchType.LAZY)
    private List<Organization> organizations;

    public Right() {
    }

    public Right(String right_name, String right_description) {
        this.right_name = right_name;
        this.right_description = right_description;
    }

    public long getRight_id() {
        return right_id;
    }

    public void setRight_id(long right_id) {
        this.right_id = right_id;
    }

    public String getRight_name() {
        return right_name;
    }

    public void setRight_name(String right_name) {
        this.right_name = right_name;
    }

    public String getRight_description() {
        return right_description;
    }

    public void setRight_description(String right_description) {
        this.right_description = right_description;
    }
}
