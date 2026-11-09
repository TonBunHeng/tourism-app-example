package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessImage implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("business_id")
    private Long businessId;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("caption")
    private String caption;

    @SerializedName("is_cover")
    private Boolean isCover;

    @SerializedName("display_order")
    private Integer displayOrder;

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

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption != null ? caption : "";
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isCover() {
        return isCover != null && isCover;
    }

    public void setCover(Boolean cover) {
        isCover = cover;
    }

    public Integer getDisplayOrder() {
        return displayOrder != null ? displayOrder : 0;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}

