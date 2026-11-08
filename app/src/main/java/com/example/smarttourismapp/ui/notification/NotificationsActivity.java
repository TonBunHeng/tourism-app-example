package com.example.smarttourismapp.ui.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.R;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.databinding.ActivityNotificationsBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private ActivityNotificationsBinding binding;
    private ApiService apiService;
    private NotificationAdapter notificationAdapter;
    private final List<NotificationItem> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadNotifications();
        loadUnreadCount();

        binding.swipeRefresh.setOnRefreshListener(this::loadNotifications);
        binding.btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(this);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(notificationAdapter);
    }

    private void loadNotifications() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        apiService.getNotifications(null, null, null).enqueue(new Callback<ApiResponse<List<NotificationItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<NotificationItem>>> call, @NonNull Response<ApiResponse<List<NotificationItem>>> response) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    notificationsList.clear();
                    notificationsList.addAll(response.body().getData());
                    notificationAdapter.submitList(new ArrayList<>(notificationsList));

                    if (notificationsList.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutEmpty.setVisibility(View.GONE);
                    }
                } else {
                    if (notificationsList.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<NotificationItem>>> call, @NonNull Throwable t) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (notificationsList.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadUnreadCount() {
        apiService.getUnreadNotificationsCount().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Object countObj = response.body().getData().get("count");
                    int count = 0;
                    if (countObj instanceof Number) {
                        count = ((Number) countObj).intValue();
                    }
                    if (count > 0) {
                        binding.tvUnreadBadge.setText(count + " unread notifications");
                        binding.btnMarkAllRead.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvUnreadBadge.setText("All caught up!");
                        binding.btnMarkAllRead.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Throwable t) {
                // Ignore silently
            }
        });
    }

    private void markAllAsRead() {
        apiService.markAllNotificationsRead().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                Toast.makeText(NotificationsActivity.this, "All notifications marked as read.", Toast.LENGTH_SHORT).show();
                for (NotificationItem item : notificationsList) {
                    item.setRead(true);
                }
                notificationAdapter.submitList(new ArrayList<>(notificationsList));
                binding.tvUnreadBadge.setText("All caught up!");
                binding.btnMarkAllRead.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Failed to mark all as read.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotificationClick(NotificationItem item) {
        if (item == null) return;
        if (!item.isRead() && item.getId() != null) {
            apiService.markNotificationRead(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                    item.setRead(true);
                    notificationAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {}
            });
        }
    }

    @Override
    public void onDeleteClick(NotificationItem item) {
        if (item == null || item.getId() == null) return;

        apiService.deleteNotification(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                notificationsList.remove(item);
                notificationAdapter.submitList(new ArrayList<>(notificationsList));
                Toast.makeText(NotificationsActivity.this, "Notification deleted.", Toast.LENGTH_SHORT).show();
                if (notificationsList.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Could not delete notification.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
