package com.example.smarttourismapp.ui.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.databinding.ActivityBusinessesBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Business;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessesActivity extends AppCompatActivity implements BusinessAdapter.OnBusinessClickListener {

    private ActivityBusinessesBinding binding;
    private ApiService apiService;
    private BusinessAdapter businessAdapter;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBusinessesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(this);

        setupRecyclerView();
        setupListeners();
        loadBusinesses(null);
    }

    private void setupRecyclerView() {
        businessAdapter = new BusinessAdapter(this);
        binding.rvBusinesses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBusinesses.setAdapter(businessAdapter);

        binding.swipeRefreshBusinesses.setOnRefreshListener(() -> {
            String query = binding.etBusinessesSearch.getText() != null
                    ? binding.etBusinessesSearch.getText().toString().trim()
                    : null;
            loadBusinesses(query);
            binding.swipeRefreshBusinesses.setRefreshing(false);
        });
    }

    private void setupListeners() {
        binding.btnBusinessesBack.setOnClickListener(v -> finish());

        binding.etBusinessesSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.etBusinessesSearch.getText() != null
                        ? binding.etBusinessesSearch.getText().toString().trim()
                        : null;
                loadBusinesses(query);
                return true;
            }
            return false;
        });

        binding.etBusinessesSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    loadBusinesses(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadBusinesses(String search) {
        binding.loadingBusinesses.setVisibility(View.VISIBLE);
        binding.tvBusinessesEmpty.setVisibility(View.GONE);

        apiService.getBusinesses(null, null, search, null, 1, 30)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                            Response<ApiResponse<Map<String, Object>>> response) {
                        binding.loadingBusinesses.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            Map<String, Object> dataMap = response.body().getData();
                            Object rawList = dataMap.get("businesses");
                            if (rawList != null) {
                                String json = gson.toJson(rawList);
                                Type listType = new TypeToken<List<Business>>() {
                                }.getType();
                                List<Business> list = gson.fromJson(json, listType);
                                businessAdapter.submitList(list != null ? list : new ArrayList<>());
                                if (list == null || list.isEmpty()) {
                                    binding.tvBusinessesEmpty.setVisibility(View.VISIBLE);
                                }
                                return;
                            }
                        }
                        binding.tvBusinessesEmpty.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        binding.loadingBusinesses.setVisibility(View.GONE);
                        binding.tvBusinessesEmpty.setText("Unable to load businesses. Please check your connection.");
                        binding.tvBusinessesEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onBusinessClick(Business business) {
        if (business != null) {
            Intent intent = new Intent(this, BusinessDetailActivity.class);
            intent.putExtra(BusinessDetailActivity.EXTRA_BUSINESS, business);
            if (business.getId() != null) {
                intent.putExtra(BusinessDetailActivity.EXTRA_BUSINESS_ID, business.getId());
            }
            startActivity(intent);
        }
    }
}
