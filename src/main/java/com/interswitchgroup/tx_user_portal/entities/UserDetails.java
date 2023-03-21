package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_details_id;

    @Column(name = "user_designation")
    private String designation;

    @Column(name = "user_department")
    private String department;

    @Column(name = "office_number")
    private String officeNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    public UserDetails() {
    }

    public UserDetails(String designation, String department, User user, String officeNumber, Organization organization, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.designation = designation;
        this.department = department;
        this.user = user;
        this.officeNumber = officeNumber;
        this.organization = organization;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    public Long getUser_details_id() {
        return user_details_id;
    }

    public void setUser_details_id(Long user_details_id) {
        this.user_details_id = user_details_id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
}
