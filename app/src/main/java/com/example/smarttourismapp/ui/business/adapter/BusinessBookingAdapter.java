package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.Booking;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessBookingAdapter extends RecyclerView.Adapter<BusinessBookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onConfirm(Booking booking);
        void onReject(Booking booking);
        void onComplete(Booking booking);
        void onDetails(Booking booking);
    }

    private final Context context;
    private final List<Booking> bookingList = new ArrayList<>();
    private final OnBookingActionListener listener;

    public BusinessBookingAdapter(Context context, OnBookingActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setBookings(List<Booking> list) {
        bookingList.clear();
        if (list != null) {
            bookingList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvBookingRef.setText(booking.getBookingReference());
        holder.tvCustomerName.setText(booking.getCustomerName());

        String sName = booking.getServiceName();
        if (sName.isEmpty() && booking.getBusiness() != null) {
            sName = booking.getBusiness().getName();
        }
        holder.tvServiceName.setText(sName.isEmpty() ? "Service Reservation" : sName);

        String dateTime = "📅 " + booking.getBookingDate();
        if (!booking.getBookingTime().isEmpty()) {
            dateTime += " • " + booking.getBookingTime();
        }
        holder.tvBookingDateTime.setText(dateTime);

        holder.tvBookingGuests.setText(String.format(Locale.US, "👥 %d Guests", booking.getNumberOfGuests()));
        holder.tvBookingPrice.setText(String.format(Locale.US, "Total: $%.2f", booking.getTotalPrice()));

        // Payment status badge
        String payment = booking.getPaymentStatus();
        if ("paid".equalsIgnoreCase(payment)) {
            holder.tvPaymentStatus.setText("Paid");
            holder.tvPaymentStatus.setBackgroundColor(Color.parseColor("#DCFCE7"));
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#166534"));
        } else {
            holder.tvPaymentStatus.setText("Unpaid");
            holder.tvPaymentStatus.setBackgroundColor(Color.parseColor("#FEF3C7"));
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#92400E"));
        }

        // Status chip & Action buttons
        String status = booking.getStatus().toLowerCase();
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnComplete.setVisibility(View.GONE);

        if ("confirmed".equals(status)) {
            holder.chipBookingStatus.setText("Confirmed");
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.info);
            holder.btnComplete.setVisibility(View.VISIBLE);
        } else if ("pending".equals(status)) {
            holder.chipBookingStatus.setText("Pending Action");
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.warning);
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else if ("completed".equals(status)) {
            holder.chipBookingStatus.setText("Completed");
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.success);
        } else if ("cancelled".equals(status)) {
            holder.chipBookingStatus.setText("Cancelled");
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.error);
        } else if ("rejected".equals(status)) {
            holder.chipBookingStatus.setText("Declined");
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.error);
        } else {
            holder.chipBookingStatus.setText(booking.getStatus());
            holder.chipBookingStatus.setChipBackgroundColorResource(R.color.surface);
        }

        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onConfirm(booking);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(booking);
        });

        holder.btnComplete.setOnClickListener(v -> {
            if (listener != null) listener.onComplete(booking);
        });

        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onDetails(booking);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDetails(booking);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingRef, tvCustomerName, tvServiceName, tvBookingDateTime, tvBookingGuests, tvBookingPrice, tvPaymentStatus;
        Chip chipBookingStatus;
        MaterialButton btnDetails, btnReject, btnConfirm, btnComplete;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingRef = itemView.findViewById(R.id.tvBookingRef);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvBookingDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvBookingGuests = itemView.findViewById(R.id.tvBookingGuests);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            chipBookingStatus = itemView.findViewById(R.id.chipBookingStatus);
            btnDetails = itemView.findViewById(R.id.btnBookingDetails);
            btnReject = itemView.findViewById(R.id.btnRejectBooking);
            btnConfirm = itemView.findViewById(R.id.btnConfirmBooking);
            btnComplete = itemView.findViewById(R.id.btnCompleteBooking);
        }
    }
}

