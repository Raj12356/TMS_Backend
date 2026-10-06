package com.tms.dto;

import java.time.Instant;

public class CommentDto {
    private Long id;
    private Integer userId;
    private String userName;
    private String message;
    private String createdAt;

    public CommentDto() {
    }

    public CommentDto(Long id, Integer userId, String userName, String message, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.createdAt = createdAt != null ? createdAt.toString() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

