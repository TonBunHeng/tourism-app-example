package com.example.tourism_app.model;

public class EventItem {
    private String id;
    private String title;
    private String location;
    private String date;
    private int imageResId;

    public EventItem(String id, String title, String location, String date, int imageResId) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public int getImageResId() { return imageResId; }
}