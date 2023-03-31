package com.interswitchgroup.tx_user_portal.models.request;

public class CommentRequestModel {
    private String comment_message;

    public CommentRequestModel() {
    }

    public CommentRequestModel(String comment_message) {
        this.comment_message = comment_message;
    }

    public String getComment_message() {
        return comment_message;
    }

    public void setComment_message(String comment_message) {
        this.comment_message = comment_message;
    }
}
