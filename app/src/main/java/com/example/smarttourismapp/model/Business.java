package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Business implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("owner_id")
    private Long ownerId;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private Object category;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("province")
    private Object province;

    @SerializedName("province_name")
    private String provinceName;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("website")
    private String website;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("rating")
    private float rating;

    @SerializedName("reviews_count")
    private int reviewsCount;

    @SerializedName("status")
    private String status;

    @SerializedName("verification_status")
    private String verificationStatus;

    @SerializedName("images")
    private java.util.List<BusinessImage> images;

    @SerializedName("services")
    private java.util.List<BusinessService> services;

    @SerializedName("hours")
    private java.util.List<BusinessHour> hours;

    @SerializedName("promotions")
    private java.util.List<BusinessPromotion> promotions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryString() {
        if (categoryName != null && !categoryName.isEmpty()) return categoryName;
        if (category instanceof String) return (String) category;
        return "Business Service";
    }

    public String getCategory() {
        return getCategoryString();
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public String getProvinceString() {
        if (provinceName != null && !provinceName.isEmpty()) return provinceName;
        if (province instanceof String) return (String) province;
        return "Cambodia";
    }

    public String getProvince() {
        return getProvinceString();
    }

    public void setProvince(Object province) {
        this.province = province;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating > 0 ? rating : 4.6f;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isVerified() {
        return "approved".equalsIgnoreCase(verificationStatus);
    }

    public java.util.List<BusinessImage> getImages() {
        return images != null ? images : new java.util.ArrayList<>();
    }

    public void setImages(java.util.List<BusinessImage> images) {
        this.images = images;
    }

    public java.util.List<BusinessService> getServices() {
        return services != null ? services : new java.util.ArrayList<>();
    }

    public void setServices(java.util.List<BusinessService> services) {
        this.services = services;
    }

    public java.util.List<BusinessHour> getHours() {
        return hours != null ? hours : new java.util.ArrayList<>();
    }

    public void setHours(java.util.List<BusinessHour> hours) {
        this.hours = hours;
    }

    public java.util.List<BusinessPromotion> getPromotions() {
        return promotions != null ? promotions : new java.util.ArrayList<>();
    }

    public void setPromotions(java.util.List<BusinessPromotion> promotions) {
        this.promotions = promotions;
    }
}
