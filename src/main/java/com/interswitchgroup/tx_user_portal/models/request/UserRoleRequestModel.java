package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class UserRoleRequestModel {
    private long userId;
    private List<Integer> roleIds;

    public UserRoleRequestModel(long userId, List<Integer> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
