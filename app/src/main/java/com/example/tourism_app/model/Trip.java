package com.example.tourism_app.model;

import java.io.Serializable;

public class Trip implements Serializable {
    private String id;
    private String title;
    private String destination;
    private String dates;
    private String status; // "Upcoming" or "Completed"
    private int imageResId;

    public Trip(String id, String title, String destination, String dates, String status, int imageResId) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.dates = dates;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDestination() { return destination; }
    public String getDates() { return dates; }
    public String getStatus() { return status; }
    public int getImageResId() { return imageResId; }
}