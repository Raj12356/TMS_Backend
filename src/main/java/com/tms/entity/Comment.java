package com.tms.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tms_comments")
public class Comment {

    @Id
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Comment() {
    }

    public Comment(Long taskId, Integer userId, String message) {
        this.id = System.currentTimeMillis();
        this.taskId = taskId;
        this.userId = userId;
        this.message = message;
        this.createdAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = System.currentTimeMillis();
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

