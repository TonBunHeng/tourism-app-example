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

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.BusinessHour;
import com.example.smarttourismapp.ui.business.adapter.BusinessHourAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusinessHoursActivity extends AppCompatActivity {

    private static final List<String> DAYS_OF_WEEK = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

    private MaterialToolbar toolbar;
    private TextView tvBusinessTitle;
    private RecyclerView rvHours;
    private ProgressBar pbLoading;
    private MaterialButton btnSave;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private BusinessHourAdapter adapter;
    private Long businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_hours);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new BusinessRepository(this);
        appPreference = new AppPreference(this);

        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;

        initViews();
        setupRecyclerView();
        loadHours();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarHours);
        tvBusinessTitle = findViewById(R.id.tvHoursBusinessTitle);
        rvHours = findViewById(R.id.rvWeeklyHours);
        pbLoading = findViewById(R.id.pbLoadingHours);
        btnSave = findViewById(R.id.btnSaveHours);

        toolbar.setNavigationOnClickListener(v -> finish());

        Business b = appPreference.getSelectedBusiness();
        if (b != null) {
            tvBusinessTitle.setText(b.getName() + " • Schedule");
        }

        btnSave.setOnClickListener(v -> saveHours());
    }

    private void setupRecyclerView() {
        adapter = new BusinessHourAdapter(this);
        rvHours.setLayoutManager(new LinearLayoutManager(this));
        rvHours.setAdapter(adapter);
    }

    private void loadHours() {
        if (businessId == null) {
            populateDefaultHours();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        repository.getHours(businessId, new BusinessRepository.RepositoryCallback<List<BusinessHour>>() {
            @Override
            public void onSuccess(List<BusinessHour> apiHours) {
                pbLoading.setVisibility(View.GONE);
                List<BusinessHour> result = new ArrayList<>();
                for (String day : DAYS_OF_WEEK) {
                    BusinessHour match = null;
                    if (apiHours != null) {
                        for (BusinessHour h : apiHours) {
                            if (day.equalsIgnoreCase(h.getDay())) {
                                match = h;
                                break;
                            }
                        }
                    }
                    if (match != null) {
                        result.add(match);
                    } else {
                        result.add(new BusinessHour(day, "08:00", "21:00", false));
                    }
                }
                adapter.setHours(result);
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                populateDefaultHours();
            }
        });
    }

    private void populateDefaultHours() {
        List<BusinessHour> list = new ArrayList<>();
        for (String day : DAYS_OF_WEEK) {
            list.add(new BusinessHour(day, "08:00", "21:00", false));
        }
        adapter.setHours(list);
    }

    private void saveHours() {
        if (businessId == null) {
            Toast.makeText(this, "No active business selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        pbLoading.setVisibility(View.VISIBLE);

        repository.updateHours(businessId, adapter.getHours(), new BusinessRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                btnSave.setEnabled(true);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(BusinessHoursActivity.this, "Operating hours saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                btnSave.setEnabled(true);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(BusinessHoursActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

