package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Province implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("icon")
    private String icon;

    @SerializedName("image")
    private String image;

    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private float rating;

    @SerializedName("places_count")
    private int placesCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        if (image != null && !image.isEmpty()) return image;
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getPlacesCount() {
        return placesCount;
    }

    public void setPlacesCount(int placesCount) {
        this.placesCount = placesCount;
    }
}

