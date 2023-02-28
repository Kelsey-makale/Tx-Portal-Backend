package com.interswitchgroup.tx_user_portal.models;

public class UserVerifyRequestModel {
    private int verification_code;
    private String email_address;

    public UserVerifyRequestModel(int verification_code, String email_address) {
        this.verification_code = verification_code;
        this.email_address = email_address;
    }

    public int getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(int verification_code) {
        this.verification_code = verification_code;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }
}
