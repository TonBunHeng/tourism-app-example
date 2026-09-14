package com.example.tourism_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tourism_app.adapter.NotificationAdapter;
import com.example.tourism_app.databinding.ActivityNotificationsBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.NotificationItem;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarNotifications, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        binding.toolbarNotifications.setNavigationOnClickListener(v -> finish());

        List<NotificationItem> notifs = MockDataProvider.getNotifications();
        if (notifs.isEmpty()) {
            binding.layoutEmptyNotifs.setVisibility(View.VISIBLE);
            binding.rvNotifications.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyNotifs.setVisibility(View.GONE);
            binding.rvNotifications.setVisibility(View.VISIBLE);

            NotificationAdapter adapter = new NotificationAdapter(notifs);
            binding.rvNotifications.setAdapter(adapter);
        }

        binding.swipeRefreshNotifs.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null) {
                binding.swipeRefreshNotifs.setRefreshing(false);
            }
        }, 1000));
    }
}