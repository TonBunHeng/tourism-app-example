package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Booking implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("booking_reference")
    private String bookingReference;

    @SerializedName("status")
    private String status;

    @SerializedName("booking_date")
    private String bookingDate;

    @SerializedName("booking_time")
    private String bookingTime;

    @SerializedName("number_of_guests")
    private int numberOfGuests;

    @SerializedName("total_price")
    private Double totalPrice;

    @SerializedName("special_requests")
    private String specialRequests;

    @SerializedName("business")
    private Business business;

    @SerializedName("service_name")
    private String serviceName;

    @SerializedName("place_name")
    private String placeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference != null ? bookingReference : (id != null ? "#BK-" + id : "#BK-REF");
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingDate() {
        return bookingDate != null ? bookingDate : "";
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime != null ? bookingTime : "";
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public int getNumberOfGuests() {
        return numberOfGuests > 0 ? numberOfGuests : 1;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getTitle() {
        if (business != null && !business.getName().isEmpty()) {
            return business.getName();
        }
        if (serviceName != null && !serviceName.isEmpty()) {
            return serviceName;
        }
        if (placeName != null && !placeName.isEmpty()) {
            return placeName;
        }
        return "Angkor Destination Visit";
    }
}
