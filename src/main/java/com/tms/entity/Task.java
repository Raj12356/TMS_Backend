package com.tms.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tms_tasks")
public class Task {

    @Id
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "assigned_to")
    private Integer assignedTo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private String dueDate;

    private String priority;

    private String category;

    @Column(nullable = false)
    private String status = "pending";

    @Column(nullable = false)
    private Boolean completed = false;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "attachments", columnDefinition = "text[]")
    private List<String> attachments = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Task() {
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = System.currentTimeMillis();
        }
        if (this.status == null) {
            this.status = Boolean.TRUE.equals(this.completed) ? "completed" : "pending";
        }
        if (this.completed == null) {
            this.completed = "completed".equalsIgnoreCase(this.status);
        }
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<String> getAttachments() {
        return attachments != null ? attachments : new ArrayList<>();
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

