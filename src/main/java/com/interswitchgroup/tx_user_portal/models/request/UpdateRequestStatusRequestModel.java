package com.interswitchgroup.tx_user_portal.models.request;

public class UpdateRequestStatusRequestModel {
    private long request_id;
    private String request_status;

    public UpdateRequestStatusRequestModel(long request_id, String request_status) {
        this.request_id = request_id;
        this.request_status = request_status;
    }

    public long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(long request_id) {
        this.request_id = request_id;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }
}
