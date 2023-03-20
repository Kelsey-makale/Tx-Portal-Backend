package com.interswitchgroup.tx_user_portal.models;

public class UserSignUpRequestModel {
    private String first_name;
    private String second_name;
    private String email_address;
    private String phone_number;
    private String password;

    public UserSignUpRequestModel(String first_name, String second_name, String email_address, String phone_number, String password) {
        this.first_name = first_name;
        this.second_name = second_name;
        this.email_address = email_address;
        this.phone_number = phone_number;
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
