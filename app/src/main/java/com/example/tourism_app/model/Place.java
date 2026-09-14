package com.example.tourism_app.model;

import java.io.Serializable;

public class Place implements Serializable {
    private String id;
    private String name;
    private String location;
    private String category;
    private double rating;
    private String price;
    private String description;
    private boolean isFavorite;
    private int imageResId;

    public Place(String id, String name, String location, String category, double rating, String price, String description, boolean isFavorite, int imageResId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.category = category;
        this.rating = rating;
        this.price = price;
        this.description = description;
        this.isFavorite = isFavorite;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public int getImageResId() { return imageResId; }
}