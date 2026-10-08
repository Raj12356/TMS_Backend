package com.tms.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskRequest {
    private Long id;
    private Integer userId;
    private Integer assignedTo;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String category;
    private String status;
    private Boolean completed;
    private List<String> attachments = new ArrayList<>();
    private Integer transferRequestedTo;
    private Integer transferRequestedBy;
    private String transferNote;

    public TaskRequest() {
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

    public Integer getTransferRequestedTo() {
        return transferRequestedTo;
    }

    public void setTransferRequestedTo(Integer transferRequestedTo) {
        this.transferRequestedTo = transferRequestedTo;
    }

    public Integer getTransferRequestedBy() {
        return transferRequestedBy;
    }

    public void setTransferRequestedBy(Integer transferRequestedBy) {
        this.transferRequestedBy = transferRequestedBy;
    }

    public String getTransferNote() {
        return transferNote;
    }

    public void setTransferNote(String transferNote) {
        this.transferNote = transferNote;
    }
}

