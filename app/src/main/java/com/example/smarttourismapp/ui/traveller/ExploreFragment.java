package com.example.smarttourismapp.ui.traveller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.FragmentExploreBinding;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.Place;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ExploreFragment extends Fragment implements PlaceAdapter.OnPlaceClickListener {

    private FragmentExploreBinding binding;
    private ExploreViewModel exploreViewModel;
    private TravelRepository travelRepository;
    private PlaceAdapter placeAdapter;

    private Long initialCategoryId = null;
    private String initialProvinceName = null;

    public void setInitialFilter(Long categoryId, String provinceName) {
        this.initialCategoryId = categoryId;
        this.initialProvinceName = provinceName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        travelRepository = new TravelRepository(requireContext());

        setupRecyclerView();
        setupSearch();
        setupFilters();
        observeViewModel();

        exploreViewModel.loadCategories();
        exploreViewModel.loadProvinces();

        if (initialCategoryId != null) {
            exploreViewModel.setCategoryFilter(initialCategoryId);
        } else {
            exploreViewModel.loadPlaces();
        }
    }

    private void setupRecyclerView() {
        placeAdapter = new PlaceAdapter(this);
        binding.rvExplorePlaces.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExplorePlaces.setAdapter(placeAdapter);

        binding.swipeRefreshExplore.setOnRefreshListener(() -> {
            exploreViewModel.loadPlaces();
            binding.swipeRefreshExplore.setRefreshing(false);
        });

        binding.btnExploreRetry.setOnClickListener(v -> {
            binding.etExploreSearch.setText("");
            binding.chipExploreAll.setChecked(true);
            exploreViewModel.setCategoryFilter(null);
            exploreViewModel.setSearchQuery(null);
            exploreViewModel.loadPlaces();
        });
    }

    private void setupSearch() {
        binding.etExploreSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.etExploreSearch.getText() != null ? binding.etExploreSearch.getText().toString().trim() : null;
                exploreViewModel.setSearchQuery(query);
                return true;
            }
            return false;
        });

        binding.etExploreSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    exploreViewModel.setSearchQuery(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        binding.chipExploreAll.setOnClickListener(v -> {
            exploreViewModel.setCategoryFilter(null);
        });

        binding.btnExploreFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        String[] sortOptions = {"Most Popular", "Highest Rated", "Newest Added", "Alphabetical (A-Z)"};
        final String[] sortKeys = {"popular", "rating", "newest", "name"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Sort Destinations")
                .setItems(sortOptions, (dialog, which) -> {
                    exploreViewModel.setSortBy(sortKeys[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        exploreViewModel.getCategoriesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                populateCategoryChips(resource.getData());
            }
        });

        exploreViewModel.getPlacesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.loadingExplore.setVisibility(View.VISIBLE);
                    binding.layoutExploreEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.loadingExplore.setVisibility(View.GONE);
                    List<Place> places = resource.getData();
                    placeAdapter.setPlaces(places);
                    if (places == null || places.isEmpty()) {
                        binding.layoutExploreEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutExploreEmpty.setVisibility(View.GONE);
                    }
                    break;
                case ERROR:
                    binding.loadingExplore.setVisibility(View.GONE);
                    binding.layoutExploreEmpty.setVisibility(View.VISIBLE);
                    binding.tvExploreEmptyTitle.setText("Unable to load destinations");
                    binding.tvExploreEmptySubtitle.setText(resource.getMessage() != null ? resource.getMessage() : "Please check your internet connection.");
                    break;
            }
        });
    }

    private void populateCategoryChips(List<Category> categories) {
        if (categories == null || binding == null) return;

        for (Category category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setClickable(true);

            if (initialCategoryId != null && initialCategoryId.equals(category.getId())) {
                chip.setChecked(true);
            }

            chip.setOnClickListener(v -> {
                exploreViewModel.setCategoryFilter(category.getId());
            });

            binding.chipGroupExploreCategories.addView(chip);
        }
    }

    @Override
    public void onPlaceClick(Place place) {
        if (place != null) {
            Intent intent = new Intent(requireContext(), PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE, place);
            if (place.getId() != null) {
                intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.getId());
            }
            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteClick(Place place) {
        if (place != null && place.getId() != null) {
            travelRepository.toggleFavorite(place.getId(), new TravelRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(requireContext(), "Wishlist updated!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Could not update wishlist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

