package com.example.smarttourismapp.ui.booking;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.databinding.ItemBookingBinding;
import com.example.smarttourismapp.model.Booking;

import java.util.Objects;

public class BookingAdapter extends ListAdapter<Booking, BookingAdapter.BookingViewHolder> {

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);

        void onCancelBooking(Booking booking);
    }

    private final OnBookingClickListener listener;

    public BookingAdapter(OnBookingClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Booking> DIFF_CALLBACK = new DiffUtil.ItemCallback<Booking>() {
        @Override
        public boolean areItemsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
            return Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    Objects.equals(oldItem.getBookingDate(), newItem.getBookingDate());
        }
    };

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;

        public BookingViewHolder(@NonNull ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Booking booking, OnBookingClickListener listener) {
            if (booking == null)
                return;

            binding.tvBookingRef.setText(booking.getBookingReference());
            binding.tvBookingTargetName.setText(booking.getTitle());

            String date = booking.getBookingDate();
            String time = booking.getBookingTime();
            String dateTime = date + (!time.isEmpty() ? " • " + time : "");
            binding.tvBookingDateTime.setText(dateTime);

            binding.tvBookingGuests.setText(booking.getNumberOfGuests() + " Guests");

            String status = booking.getStatus();
            binding.tvBookingStatus.setText(status.toUpperCase());

            if ("confirmed".equalsIgnoreCase(status)) {
                binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                binding.tvBookingStatus.setTextColor(Color.parseColor("#2E7D32"));
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            } else if ("pending".equalsIgnoreCase(status)) {
                binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                binding.tvBookingStatus.setTextColor(Color.parseColor("#EF6C00"));
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            } else if ("completed".equalsIgnoreCase(status)) {
                binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                binding.tvBookingStatus.setTextColor(Color.parseColor("#1565C0"));
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else {
                // Cancelled or Rejected
                binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                binding.tvBookingStatus.setTextColor(Color.parseColor("#C62828"));
                binding.btnCancelBooking.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null)
                    listener.onBookingClick(booking);
            });

            binding.btnCancelBooking.setOnClickListener(v -> {
                if (listener != null)
                    listener.onCancelBooking(booking);
            });
        }
    }
}
