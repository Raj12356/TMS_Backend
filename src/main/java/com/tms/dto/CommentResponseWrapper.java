package com.tms.dto;

public class CommentResponseWrapper {
    private CommentDto comment;

    public CommentResponseWrapper() {
    }

    public CommentResponseWrapper(CommentDto comment) {
        this.comment = comment;
    }

    public CommentDto getComment() {
        return comment;
    }

    public void setComment(CommentDto comment) {
        this.comment = comment;
    }
}

