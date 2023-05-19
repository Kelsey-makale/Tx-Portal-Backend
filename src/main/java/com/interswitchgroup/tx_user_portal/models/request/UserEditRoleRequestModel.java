package com.interswitchgroup.tx_user_portal.models.request;

import java.util.List;

public class UserEditRoleRequestModel {
    private long requestId;
    private List<Long> roleIds;

    public UserEditRoleRequestModel(long requestId, List<Long> roleIds) {
        this.requestId = requestId;
        this.roleIds = roleIds;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
