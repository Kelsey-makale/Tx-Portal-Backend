package com.interswitchgroup.tx_user_portal.entities;

import com.interswitchgroup.tx_user_portal.utils.Enums.RequestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //todo: Link to Organization table.

    @ElementCollection
    @CollectionTable(name = "request_roles", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "role_id")
    private List<Integer> roleIds;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    public Request() {
    }

    public Request(User user, List<Integer> roles, RequestStatus requestStatus, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.user = user;
        this.roleIds = roles;
        this.requestStatus = requestStatus;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
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

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
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
}
