package com.example.smarttourismapp.ui.category;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.ActivityCategoriesBinding;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.ui.traveller.CategoryAdapter;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private ActivityCategoriesBinding binding;
    private TravelRepository travelRepository;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        travelRepository = new TravelRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadCategories();

        binding.swipeRefresh.setOnRefreshListener(this::loadCategories);
        binding.btnRetry.setOnClickListener(v -> loadCategories());
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        travelRepository.getCategories(new TravelRepository.RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                categoryAdapter.submitList(categories);

                if (categories == null || categories.isEmpty()) {
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
                Toast.makeText(CategoriesActivity.this, errorMessage != null ? errorMessage : "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        if (category == null) return;
        Toast.makeText(this, "Category: " + category.getName(), Toast.LENGTH_SHORT).show();
        finish();
    }
}
