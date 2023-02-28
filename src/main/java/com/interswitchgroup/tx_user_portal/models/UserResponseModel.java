package com.interswitchgroup.tx_user_portal.models;

import java.util.Map;
import java.util.Optional;

public class UserResponseModel {
    private String status;
    private String message;
    private Optional<Map<String, Object>> data;

    public UserResponseModel(String status, String message, Optional<Map<String, Object>> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public UserResponseModel(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

