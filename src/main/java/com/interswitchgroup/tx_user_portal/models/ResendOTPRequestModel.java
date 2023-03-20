package com.interswitchgroup.tx_user_portal.models;

public class ResendOTPRequestModel {
    private String email_address;

    public ResendOTPRequestModel(String email_address) {
        this.email_address = email_address;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }
}
