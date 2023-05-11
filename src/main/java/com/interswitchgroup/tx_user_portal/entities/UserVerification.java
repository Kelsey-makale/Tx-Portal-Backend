package com.interswitchgroup.tx_user_portal.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "otp_code")
    private String otp_code;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    //@Column(name = "otp_verified", columnDefinition = "BIT DEFAULT 0")
    @Column(name = "otp_verified", columnDefinition = "boolean default false")
    private boolean otp_verified;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "expires_at")
    private LocalDateTime expires_at;


    public UserVerification() {
    }

    public UserVerification(String otp_code, User user, LocalDateTime created_at, LocalDateTime expires_at) {
        this.otp_code = otp_code;
        this.user = user;
        this.created_at = created_at;
        this.expires_at = expires_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser_id() {
        return user;
    }

    public void setUser_id(User user) {
        this.user = user;
    }

    public String getOtp_code() {
        return otp_code;
    }

    public void setOtp_code(String otp_code) {
        this.otp_code = otp_code;
    }

    public boolean isOtp_verified() {
        return otp_verified;
    }

    public void setOtp_verified(boolean otp_verified) {
        this.otp_verified = otp_verified;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }
}
