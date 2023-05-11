package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class AdminSignUpRequestModel {
    private String first_name;
    private String second_name;
    private String email_address;
    private String phone_number;
    private String password;
    private int organization_id;
    private String designation;
    private String department;
    private String office_number;
    private List<Long> roleIds;

    public AdminSignUpRequestModel(String first_name, String second_name, String email_address, String phone_number, String password, int organization_id, String designation, String department, String office_number,List<Long> roleIds) {
        this.first_name = first_name;
        this.second_name = second_name;
        this.email_address = email_address;
        this.phone_number = phone_number;
        this.password = password;
        this.organization_id = organization_id;
        this.designation = designation;
        this.department = department;
        this.office_number = office_number;
        this.roleIds = roleIds;
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

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
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

    public String getOffice_number() {
        return office_number;
    }

    public void setOffice_number(String office_number) {
        this.office_number = office_number;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

}

