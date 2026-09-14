package com.example.tourism_app.model;

public class Review {
    private String id;
    private String reviewerName;
    private float rating;
    private String date;
    private String comment;

    public Review(String id, String reviewerName, float rating, String date, String comment) {
        this.id = id;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.date = date;
        this.comment = comment;
    }

    public String getId() { return id; }
    public String getReviewerName() { return reviewerName; }
    public float getRating() { return rating; }
    public String getDate() { return date; }
    public String getComment() { return comment; }
}