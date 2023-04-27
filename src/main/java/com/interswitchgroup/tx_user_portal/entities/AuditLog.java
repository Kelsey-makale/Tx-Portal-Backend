package com.interswitchgroup.tx_user_portal.entities;

import com.interswitchgroup.tx_user_portal.utils.Enums.LogActivity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long logId;

    @Enumerated(EnumType.STRING)
    private LogActivity logActivity;

    @Column(name = "executed_by")
    private String executedBy;

    @Column(name = "details")
    private String details;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;


    public AuditLog() {
    }

    public AuditLog(LogActivity logActivity, String executedBy, String details, LocalDateTime dateCreated) {
        this.logActivity = logActivity;
        this.executedBy = executedBy;
        this.details = details;
        this.dateCreated = dateCreated;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
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
