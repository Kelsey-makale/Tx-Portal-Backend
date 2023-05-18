package com.interswitchgroup.tx_user_portal.models.request;

public class CancelRequestModel {
    private long request_id;

    public CancelRequestModel() {
    }

    public CancelRequestModel(long request_id) {
        this.request_id = request_id;
    }

    public long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(long request_id) {
        this.request_id = request_id;
    }
}
