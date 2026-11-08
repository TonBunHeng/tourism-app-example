package com.example.smarttourismapp.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.FragmentFavoritesBinding;
import com.example.smarttourismapp.model.FavoriteItem;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.ui.traveller.PlaceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteActionListener {

    private FragmentFavoritesBinding binding;
    private TravelRepository travelRepository;
    private FavoritesAdapter favoritesAdapter;

    private final List<FavoriteItem> allFavorites = new ArrayList<>();
    private String currentFilter = "all"; // "all" | "visited" | "unvisited"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        travelRepository = new TravelRepository(requireContext());

        setupRecyclerView();
        setupFilterChips();
        loadFavorites();
    }

    private void setupRecyclerView() {
        favoritesAdapter = new FavoritesAdapter(this);
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavorites.setAdapter(favoritesAdapter);

        binding.swipeRefreshFavorites.setOnRefreshListener(() -> {
            loadFavorites();
            binding.swipeRefreshFavorites.setRefreshing(false);
        });
    }

    private void setupFilterChips() {
        binding.chipFavAll.setOnClickListener(v -> applyFilter("all"));
        binding.chipFavVisited.setOnClickListener(v -> applyFilter("visited"));
        binding.chipFavUnvisited.setOnClickListener(v -> applyFilter("unvisited"));
    }

    private void applyFilter(String filter) {
        this.currentFilter = filter;
        List<FavoriteItem> filtered = new ArrayList<>();
        for (FavoriteItem item : allFavorites) {
            if ("visited".equals(filter)) {
                if (item.isVisited()) filtered.add(item);
            } else if ("unvisited".equals(filter)) {
                if (!item.isVisited()) filtered.add(item);
            } else {
                filtered.add(item);
            }
        }
        favoritesAdapter.submitList(filtered);
        if (filtered.isEmpty()) {
            binding.layoutFavoritesEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.layoutFavoritesEmpty.setVisibility(View.GONE);
        }
    }

    private void loadFavorites() {
        binding.loadingFavorites.setVisibility(View.VISIBLE);
        binding.layoutFavoritesEmpty.setVisibility(View.GONE);

        travelRepository.getFavoriteItems(new TravelRepository.RepositoryCallback<List<FavoriteItem>>() {
            @Override
            public void onSuccess(List<FavoriteItem> result) {
                if (binding == null) return;
                binding.loadingFavorites.setVisibility(View.GONE);
                allFavorites.clear();
                if (result != null) {
                    allFavorites.addAll(result);
                }
                applyFilter(currentFilter);
            }

            @Override
            public void onError(String errorMessage) {
                if (binding == null) return;
                binding.loadingFavorites.setVisibility(View.GONE);
                binding.layoutFavoritesEmpty.setVisibility(View.VISIBLE);
            }
        });
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
    public void onToggleVisited(FavoriteItem item) {
        if (item == null || item.getId() == null) return;

        travelRepository.toggleVisited(item.getId(), new TravelRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                item.setVisited(!item.isVisited());
                applyFilter(currentFilter);
                Toast.makeText(requireContext(), item.isVisited() ? "Marked as visited!" : "Marked as unvisited", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Could not update status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveFavorite(FavoriteItem item) {
        if (item == null || item.getPlaceId() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Destination")
                .setMessage("Remove this destination from your saved wishlist?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    travelRepository.removeFavorite(item.getPlaceId(), new TravelRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            allFavorites.remove(item);
                            applyFilter(currentFilter);
                            Toast.makeText(requireContext(), "Destination removed from wishlist.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Could not remove destination", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

