package com.interswitchgroup.tx_user_portal.entities;

import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "approver_id")
    private long approver_id;

    @Column(name = "organizationId")
    private long organizationId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "RequestRoles", joinColumns = {
            @JoinColumn(name = "requestId", referencedColumnName = "requestId")
    }, inverseJoinColumns = {@JoinColumn(name = "roleId", referencedColumnName = "role_id")})

    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "requestId", referencedColumnName = "requestId")
    private List<Comment> comment;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;


    public Request() {
    }

    public Request(User user, long organizationId, RequestStatus requestStatus, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.user = user;
        this.organizationId = organizationId;
        this.requestStatus = requestStatus;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public long getRequest_id() {
        return requestId;
    }

    public void setRequest_id(long request_id) {
        this.requestId = request_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
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

    public long getApprover_id() {return approver_id;}

    public void setApprover_id(long approver_id) {
        this.approver_id = approver_id;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }
}
