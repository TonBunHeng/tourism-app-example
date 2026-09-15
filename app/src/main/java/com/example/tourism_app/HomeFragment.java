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
import com.example.tourism_app.adapter.CategoryAdapter;
import com.example.tourism_app.adapter.EventAdapter;
import com.example.tourism_app.adapter.FeaturedPlaceAdapter;
import com.example.tourism_app.adapter.PlaceAdapter;
import com.example.tourism_app.databinding.FragmentHomeBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.Place;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Adjust top padding for status bar insets to prevent status bar camera cutout overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.swipeRefreshHome, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            if (binding != null) {
                int topPadding = statusBarInsets.top > 0 ? statusBarInsets.top + 8 : 12;
                binding.layoutTopHeader.setPadding(
                        binding.layoutTopHeader.getPaddingLeft(),
                        topPadding,
                        binding.layoutTopHeader.getPaddingRight(),
                        binding.layoutTopHeader.getPaddingBottom()
                );
            }
            return insets;
        });

        // Setup Categories
        CategoryAdapter categoryAdapter = new CategoryAdapter(MockDataProvider.getCategories(), (category, position) -> {
            // Filter click
        });
        binding.rvCategories.setAdapter(categoryAdapter);

        // Setup Featured Destinations
        FeaturedPlaceAdapter featuredAdapter = new FeaturedPlaceAdapter(MockDataProvider.getFeaturedPlaces(), this::openPlaceDetail);
        binding.rvFeatured.setAdapter(featuredAdapter);

        // Setup Popular Attractions
        PlaceAdapter popularAdapter = new PlaceAdapter(MockDataProvider.getPopularPlaces(), new PlaceAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(Place place) {
                openPlaceDetail(place);
            }

            @Override
            public void onFavoriteClick(Place place, int position) {
                // Handled in adapter
            }
        });
        binding.rvPopular.setAdapter(popularAdapter);

        // Setup Upcoming Events
        EventAdapter eventAdapter = new EventAdapter(MockDataProvider.getEvents());
        binding.rvEvents.setAdapter(eventAdapter);

        // Listeners
        binding.btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
            startActivity(intent);
        });

        binding.cardAiBanner.setOnClickListener(v -> openAiChat());
        binding.btnAskAi.setOnClickListener(v -> openAiChat());

        binding.swipeRefreshHome.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null) {
                binding.swipeRefreshHome.setRefreshing(false);
            }
        }, 1200));
    }

    private void openPlaceDetail(Place place) {
        Intent intent = new Intent(requireContext(), PlaceDetailActivity.class);
        intent.putExtra("extra_place", place);
        startActivity(intent);
    }

    private void openAiChat() {
        Intent intent = new Intent(requireContext(), AiChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}