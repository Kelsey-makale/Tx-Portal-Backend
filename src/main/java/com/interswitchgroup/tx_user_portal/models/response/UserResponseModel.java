package com.interswitchgroup.tx_user_portal.models.response;


import java.util.Map;
import java.util.Optional;

public class UserResponseModel {
    private int status;
    private String message;
    private Optional<Map<String, Object>> data;

    public UserResponseModel(int status, String message, Optional<Map<String, Object>> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public UserResponseModel(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Optional<Map<String, Object>> getData() {
        return data;
    }

    public void setData(Optional<Map<String, Object>> data) {
        this.data = data;
    }
}

