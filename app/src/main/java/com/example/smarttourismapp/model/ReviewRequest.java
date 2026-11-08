package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("place_id")
    private Long placeId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("title")
    private String title;

    @SerializedName("comment")
    private String comment;

    public ReviewRequest(Long placeId, int rating, String title, String comment) {
        this.placeId = placeId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

