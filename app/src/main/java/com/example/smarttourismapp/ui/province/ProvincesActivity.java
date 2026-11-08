package com.example.smarttourismapp.ui.province;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.ActivityProvincesBinding;
import com.example.smarttourismapp.model.Province;
import com.example.smarttourismapp.ui.traveller.ProvinceAdapter;

import java.util.List;

public class ProvincesActivity extends AppCompatActivity implements ProvinceAdapter.OnProvinceClickListener {

    private ActivityProvincesBinding binding;
    private TravelRepository travelRepository;
    private ProvinceAdapter provinceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProvincesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        travelRepository = new TravelRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadProvinces();

        binding.swipeRefresh.setOnRefreshListener(this::loadProvinces);
        binding.btnRetry.setOnClickListener(v -> loadProvinces());
    }

    private void setupRecyclerView() {
        provinceAdapter = new ProvinceAdapter(this);
        binding.rvProvinces.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProvinces.setAdapter(provinceAdapter);
    }

    private void loadProvinces() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        travelRepository.getProvinces(new TravelRepository.RepositoryCallback<List<Province>>() {
            @Override
            public void onSuccess(List<Province> provinces) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                provinceAdapter.submitList(provinces);

                if (provinces == null || provinces.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(ProvincesActivity.this, errorMessage != null ? errorMessage : "Failed to load provinces", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProvinceClick(Province province) {
        if (province == null) return;
        Toast.makeText(this, "Province: " + province.getName(), Toast.LENGTH_SHORT).show();
        finish();
    }
}
