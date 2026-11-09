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

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("special_requests")
    private String specialRequests;

    @SerializedName("rejection_reason")
    private String rejectionReason;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_email")
    private String customerEmail;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("user")
    private User user;

    @SerializedName("business")
    private Business business;

    @SerializedName("service_name")
    private String serviceName;

    @SerializedName("service")
    private BusinessService service;

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
        return totalPrice != null ? totalPrice : 0.0;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : "unpaid";
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getSpecialRequests() {
        return specialRequests != null ? specialRequests : "";
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getRejectionReason() {
        return rejectionReason != null ? rejectionReason : "";
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getCustomerName() {
        if (customerName != null && !customerName.isEmpty()) return customerName;
        if (user != null && !user.getName().isEmpty()) return user.getName();
        return "Customer";
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        if (customerEmail != null && !customerEmail.isEmpty()) return customerEmail;
        if (user != null && !user.getEmail().isEmpty()) return user.getEmail();
        return "";
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        if (customerPhone != null && !customerPhone.isEmpty()) return customerPhone;
        if (user != null && user.getPhone() != null) return user.getPhone();
        return "";
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getServiceName() {
        if (serviceName != null && !serviceName.isEmpty()) return serviceName;
        if (service != null && !service.getName().isEmpty()) return service.getName();
        return "";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BusinessService getService() {
        return service;
    }

    public void setService(BusinessService service) {
        this.service = service;
    }

    public String getTitle() {
        if (serviceName != null && !serviceName.isEmpty()) {
            return serviceName;
        }
        if (service != null && !service.getName().isEmpty()) {
            return service.getName();
        }
        if (business != null && !business.getName().isEmpty()) {
            return business.getName();
        }
        if (placeName != null && !placeName.isEmpty()) {
            return placeName;
        }
        return "Travel Reservation";
    }
}
