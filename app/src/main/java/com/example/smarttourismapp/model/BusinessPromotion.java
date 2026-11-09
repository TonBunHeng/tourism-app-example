package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessPromotion implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("business_id")
    private Long businessId;

    @SerializedName("title")
    private String title;

    @SerializedName("code")
    private String code;

    @SerializedName("discount_percent")
    private Double discountPercent;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("description")
    private String description;

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

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code != null ? code : "";
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDiscountPercent() {
        return discountPercent != null ? discountPercent : 0.0;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getStartDate() {
        return startDate != null ? startDate : "";
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate != null ? endDate : "";
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive == null || isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

