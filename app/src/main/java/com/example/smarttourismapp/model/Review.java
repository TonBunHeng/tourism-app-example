package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Review implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("place_id")
    private Long placeId;

    @SerializedName("business_id")
    private Long businessId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("title")
    private String title;

    @SerializedName("comment")
    private String comment;

    @SerializedName("owner_reply")
    private String ownerReply;

    @SerializedName("reply")
    private String reply;

    @SerializedName("replied_at")
    private String repliedAt;

    @SerializedName("user")
    private User user;

    @SerializedName("created_at")
    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment != null ? comment : "";
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwnerReply() {
        if (ownerReply != null && !ownerReply.trim().isEmpty()) return ownerReply;
        if (reply != null && !reply.trim().isEmpty()) return reply;
        return null;
    }

    public void setOwnerReply(String ownerReply) {
        this.ownerReply = ownerReply;
    }

    public String getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(String repliedAt) {
        this.repliedAt = repliedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "";
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
