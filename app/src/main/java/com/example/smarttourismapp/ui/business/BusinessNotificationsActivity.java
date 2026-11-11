package com.example.smarttourismapp.ui.business;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.NotificationItem;
import com.example.smarttourismapp.ui.notification.NotificationAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessNotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private MaterialToolbar toolbar;
    private TextView tvUnreadCount;
    private MaterialButton btnMarkAllRead;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View emptyView;
    private ProgressBar progressBar;

    private ApiService apiService;
    private NotificationAdapter adapter;
    private final List<NotificationItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(this);

        initViews();
        setupRecyclerView();
        loadNotifications();
        loadUnreadCount();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarNotifications);
        tvUnreadCount = findViewById(R.id.tvUnreadBadgeCount);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        swipeRefresh = findViewById(R.id.swipeRefreshBizNotifications);
        recyclerView = findViewById(R.id.rvBizNotifications);
        emptyView = findViewById(R.id.layoutEmptyBizNotifications);
        progressBar = findViewById(R.id.pbLoadingBizNotifications);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        swipeRefresh.setOnRefreshListener(this::loadNotifications);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getNotifications(null, null, null).enqueue(new Callback<ApiResponse<List<NotificationItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationItem>>> call, Response<ApiResponse<List<NotificationItem>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                list.clear();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    list.addAll(response.body().getData());
                }
                adapter.submitList(new ArrayList<>(list));

                if (list.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationItem>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadUnreadCount() {
        apiService.getUnreadNotificationsCount().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Object countObj = response.body().getData().get("unread_count");
                    if (countObj instanceof Number) {
                        int count = ((Number) countObj).intValue();
                        tvUnreadCount.setText(count > 0 ? count + " unread updates" : "All updates read");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void markAllAsRead() {
        apiService.markAllNotificationsRead().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BusinessNotificationsActivity.this, "All notifications marked as read.", Toast.LENGTH_SHORT).show();
                    loadNotifications();
                    loadUnreadCount();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    @Override
    public void onNotificationClick(NotificationItem notification) {
        if (!notification.isRead()) {
            apiService.markNotificationRead(notification.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    notification.setReadAt("now");
                    adapter.notifyDataSetChanged();
                    loadUnreadCount();
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        }
    }

    @Override
    public void onDeleteClick(NotificationItem notification) {
        apiService.deleteNotification(notification.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                list.remove(notification);
                adapter.submitList(new ArrayList<>(list));
                loadUnreadCount();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }
}
