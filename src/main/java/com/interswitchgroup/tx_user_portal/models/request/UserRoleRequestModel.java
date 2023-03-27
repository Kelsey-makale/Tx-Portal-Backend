package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class UserRoleRequestModel {
    private long userId;
    private List<Long> roleIds;

    public UserRoleRequestModel(long userId, List<Long> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
