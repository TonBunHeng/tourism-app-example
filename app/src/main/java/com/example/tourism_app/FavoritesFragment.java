package com.example.tourism_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.tourism_app.adapter.PlaceAdapter;
import com.example.tourism_app.databinding.FragmentFavoritesBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.Place;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarFavorites, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        List<Place> favoritePlaces = MockDataProvider.getFeaturedPlaces(); // Using mock places
        if (favoritePlaces.isEmpty()) {
            binding.layoutEmptyFavorites.setVisibility(View.VISIBLE);
            binding.rvFavorites.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyFavorites.setVisibility(View.GONE);
            binding.rvFavorites.setVisibility(View.VISIBLE);

            PlaceAdapter adapter = new PlaceAdapter(favoritePlaces, new PlaceAdapter.OnPlaceClickListener() {
                @Override
                public void onPlaceClick(Place place) {
                    Intent intent = new Intent(requireContext(), PlaceDetailActivity.class);
                    intent.putExtra("extra_place", place);
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(Place place, int position) {
                    // Toggle favorite logic
                }
            });
            binding.rvFavorites.setAdapter(adapter);
        }

        binding.swipeRefreshFavorites.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null) {
                binding.swipeRefreshFavorites.setRefreshing(false);
            }
        }, 1000));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}