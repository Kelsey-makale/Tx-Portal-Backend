package com.interswitchgroup.tx_user_portal.models;

public class RequestWithUserAndRolesDto {
    private long requestId;
    private String userName;
    private String roleName;

    // Getters and setters


    public RequestWithUserAndRolesDto() {
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
