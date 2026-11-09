package com.example.smarttourismapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessBookingStats implements Serializable {
    @SerializedName("total_bookings")
    private int totalBookings;

    @SerializedName("pending_bookings")
    private int pendingBookings;

    @SerializedName("confirmed_bookings")
    private int confirmedBookings;

    @SerializedName("completed_bookings")
    private int completedBookings;

    @SerializedName("cancelled_bookings")
    private int cancelledBookings;

    @SerializedName("rejected_bookings")
    private int rejectedBookings;

    @SerializedName("upcoming_bookings")
    private int upcomingBookings;

    @SerializedName("total_revenue")
    private double totalRevenue;

    @SerializedName("currency")
    private String currency;

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(int pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public int getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(int confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public int getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(int completedBookings) {
        this.completedBookings = completedBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(int cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public int getRejectedBookings() {
        return rejectedBookings;
    }

    public void setRejectedBookings(int rejectedBookings) {
        this.rejectedBookings = rejectedBookings;
    }

    public int getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(int upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getCurrency() {
        return currency != null ? currency : "USD";
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

