package com.interswitchgroup.tx_user_portal.entities;

import com.interswitchgroup.tx_user_portal.utils.Enums.LogActivity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long logId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    private LogActivity logActivity;

    @Column(name = "details")
    private String details;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;


    public AuditLog() {
    }

    public AuditLog(long logId, User user, LogActivity logActivity, String details, LocalDateTime dateCreated) {
        this.logId = logId;
        this.user = user;
        this.logActivity = logActivity;
        this.details = details;
        this.dateCreated = dateCreated;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LogActivity getLogActivity() {
        return logActivity;
    }

    public void setLogActivity(LogActivity logActivity) {
        this.logActivity = logActivity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
