package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.databinding.ItemNotificationBinding;
import com.example.tourism_app.model.NotificationItem;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private final List<NotificationItem> notifications;

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new NotifViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        NotifViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NotificationItem notif) {
            binding.tvNotifTitle.setText(notif.getTitle());
            binding.tvNotifMessage.setText(notif.getMessage());
            binding.tvNotifTime.setText(notif.getTime());
            binding.vUnreadDot.setVisibility(notif.isUnread() ? View.VISIBLE : View.GONE);
        }
    }
}