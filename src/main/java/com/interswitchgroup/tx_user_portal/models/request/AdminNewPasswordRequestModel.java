package com.interswitchgroup.tx_user_portal.models.request;

public class AdminNewPasswordRequestModel {
    private String verification_code;
    private String email_address;
    private String new_password;

    public AdminNewPasswordRequestModel(String verification_code, String email_address, String new_password) {
        this.verification_code = verification_code;
        this.email_address = email_address;
        this.new_password = new_password;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
