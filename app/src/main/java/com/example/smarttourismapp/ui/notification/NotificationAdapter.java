package com.example.smarttourismapp.ui.notification;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.databinding.ItemNotificationBinding;
import com.example.smarttourismapp.model.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends ListAdapter<NotificationItem, NotificationAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
        void onDeleteClick(NotificationItem item);
    }

    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<NotificationItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationItem oldItem, @NonNull NotificationItem newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationItem oldItem, @NonNull NotificationItem newItem) {
            return oldItem.isRead() == newItem.isRead() &&
                    Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getMessage(), newItem.getMessage());
        }
    };

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationItem item, OnNotificationClickListener listener) {
            if (item == null) return;

            binding.tvNotificationTitle.setText(item.getTitle());

            String message = item.getMessage();
            if (message != null && !message.trim().isEmpty()) {
                binding.tvNotificationMessage.setText(message);
                binding.tvNotificationMessage.setVisibility(View.VISIBLE);
            } else {
                binding.tvNotificationMessage.setVisibility(View.GONE);
            }

            binding.tvNotificationCategory.setText(item.getCategory().toUpperCase());
            binding.tvNotificationTime.setText(formatNotificationTime(item.getCreatedAt()));

            if (item.isRead()) {
                binding.viewUnreadDot.setVisibility(View.GONE);
                binding.cardNotification.setCardBackgroundColor(Color.WHITE);
                binding.cardNotification.setStrokeColor(Color.parseColor("#E2E8F0"));
            } else {
                binding.viewUnreadDot.setVisibility(View.VISIBLE);
                binding.cardNotification.setCardBackgroundColor(Color.parseColor("#F0F7FF"));
                binding.cardNotification.setStrokeColor(Color.parseColor("#BAE6FD"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onNotificationClick(item);
            });

            binding.btnDeleteNotification.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(item);
            });
        }

        private static String formatNotificationTime(String rawDate) {
            if (rawDate == null || rawDate.trim().isEmpty()) {
                return "";
            }
            try {
                SimpleDateFormat isoFormat;
                if (rawDate.contains(".")) {
                    isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
                } else {
                    isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                }
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(rawDate);
                if (date == null) return rawDate;

                long diffMillis = System.currentTimeMillis() - date.getTime();
                if (diffMillis < 0) diffMillis = 0;

                long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
                long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
                long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

                if (diffMinutes < 1) {
                    return "Just now";
                } else if (diffMinutes < 60) {
                    return diffMinutes + "m ago";
                } else if (diffHours < 24) {
                    return diffHours + "h ago";
                } else if (diffDays < 7) {
                    return diffDays + "d ago";
                } else {
                    SimpleDateFormat outFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                    return outFormat.format(date);
                }
            } catch (Exception e) {
                if (rawDate.length() >= 10 && rawDate.charAt(4) == '-' && rawDate.charAt(7) == '-') {
                    return rawDate.substring(0, 10);
                }
                return rawDate;
            }
        }
    }
}

