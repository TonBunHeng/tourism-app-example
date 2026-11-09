package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessHour implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("business_id")
    private Long businessId;

    @SerializedName("day")
    private String day;

    @SerializedName("open_time")
    private String openTime;

    @SerializedName("close_time")
    private String closeTime;

    @SerializedName("is_closed")
    private Boolean isClosed;

    public BusinessHour() {}

    public BusinessHour(String day, String openTime, String closeTime, boolean isClosed) {
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed;
    }

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

    public String getDay() {
        return day != null ? day : "";
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOpenTime() {
        return openTime != null ? openTime : "08:00";
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime != null ? closeTime : "21:00";
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClosed() {
        return isClosed != null && isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }
}

