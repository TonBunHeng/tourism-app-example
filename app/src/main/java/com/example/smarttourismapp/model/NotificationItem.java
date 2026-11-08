package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("category")
    private String category;

    @SerializedName("read_at")
    private String readAt;

    @SerializedName("is_read")
    private Boolean isRead;

    @SerializedName("created_at")
    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "Notification";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category != null ? category : "General";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRead() {
        if (isRead != null) return isRead;
        return readAt != null && !readAt.isEmpty();
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "";
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

