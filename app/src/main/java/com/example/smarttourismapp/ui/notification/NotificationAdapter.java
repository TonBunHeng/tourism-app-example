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

import java.util.Objects;

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
            binding.tvNotificationMessage.setText(item.getMessage());
            binding.tvNotificationCategory.setText(item.getCategory().toUpperCase());
            binding.tvNotificationTime.setText(item.getCreatedAt());

            if (item.isRead()) {
                binding.viewUnreadDot.setVisibility(View.GONE);
                binding.cardNotification.setCardBackgroundColor(Color.TRANSPARENT);
            } else {
                binding.viewUnreadDot.setVisibility(View.VISIBLE);
                binding.cardNotification.setCardBackgroundColor(Color.parseColor("#F8FAFC"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onNotificationClick(item);
            });

            binding.btnDeleteNotification.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(item);
            });
        }
    }
}

