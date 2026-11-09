package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessService implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("business_id")
    private Long businessId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Double price;

    @SerializedName("duration")
    private String duration;

    @SerializedName("capacity")
    private Integer capacity;

    @SerializedName("status")
    private String status;

    @SerializedName("is_active")
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price != null ? price : 0.0;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDuration() {
        return duration != null ? duration : "1 hour";
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getCapacity() {
        return capacity != null && capacity > 0 ? capacity : 1;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status != null ? status : (isActive() ? "active" : "inactive");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        if (isActive != null) return isActive;
        return "active".equalsIgnoreCase(status);
    }

    public void setActive(Boolean active) {
        isActive = active;
        this.status = Boolean.TRUE.equals(active) ? "active" : "inactive";
    }
}

