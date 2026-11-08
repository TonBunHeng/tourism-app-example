package com.example.smarttourismapp.ui.traveller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.FragmentHomeBinding;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Province;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.notification.NotificationsActivity;
import com.example.smarttourismapp.utils.AppPreference;

public class HomeFragment extends Fragment implements PlaceAdapter.OnPlaceClickListener {

    public interface HomeNavigationListener {
        void navigateToExplore(Long categoryId, String provinceName);
    }

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private AppPreference appPreference;
    private TravelRepository travelRepository;

    private CategoryAdapter categoryAdapter;
    private ProvinceAdapter provinceAdapter;
    private PlaceAdapter featuredPlaceAdapter;
    private PlaceAdapter popularPlaceAdapter;

    private HomeNavigationListener navigationListener;

    public void setNavigationListener(HomeNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        appPreference = new AppPreference(requireContext());
        travelRepository = new TravelRepository(requireContext());

        setupUserInfo();
        setupAdapters();
        setupListeners();
        observeViewModel();

        homeViewModel.loadHomeData();
    }

    private void setupUserInfo() {
        User user = appPreference.getUser();
        if (user != null && !TextUtils.isEmpty(user.getName())) {
            binding.tvHomeGreeting.setText("Hello, " + user.getName() + "!");
            if (!TextUtils.isEmpty(user.getLocation())) {
                binding.tvHomeLocationSubtitle.setText("Exploring from " + user.getLocation());
            }
        }
    }

    private void setupAdapters() {
        // Categories Carousel
        categoryAdapter = new CategoryAdapter(category -> {
            if (navigationListener != null) {
                navigationListener.navigateToExplore(category.getId(), null);
            }
        });
        binding.rvHomeCategories.setAdapter(categoryAdapter);

        // Provinces Carousel
        provinceAdapter = new ProvinceAdapter(province -> {
            if (navigationListener != null) {
                navigationListener.navigateToExplore(null, province.getName());
            }
        });
        binding.rvHomeProvinces.setAdapter(provinceAdapter);

        // Featured Places Carousel
        featuredPlaceAdapter = new PlaceAdapter(this);
        binding.rvHomeFeatured.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvHomeFeatured.setAdapter(featuredPlaceAdapter);

        // Popular Places List
        popularPlaceAdapter = new PlaceAdapter(this);
        binding.rvHomePopular.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHomePopular.setAdapter(popularPlaceAdapter);
    }

    private void setupListeners() {
        binding.swipeRefreshHome.setOnRefreshListener(() -> {
            homeViewModel.loadHomeData();
            binding.swipeRefreshHome.setRefreshing(false);
        });

        binding.cardHomeSearch.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToExplore(null, null);
            }
        });

        binding.tvViewAllCategories.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smarttourismapp.ui.category.CategoriesActivity.class));
        });

        binding.tvViewAllFeatured.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToExplore(null, null);
            }
        });

        binding.tvViewAllProvinces.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smarttourismapp.ui.province.ProvincesActivity.class));
        });

        binding.btnHomeNotifications.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NotificationsActivity.class));
        });

        binding.btnQuickHotels.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smarttourismapp.ui.business.BusinessesActivity.class));
        });

        binding.btnQuickProvinces.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smarttourismapp.ui.province.ProvincesActivity.class));
        });

        binding.btnQuickBookings.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToExplore(null, null);
            }
        });

        binding.btnQuickAiChat.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.smarttourismapp.ui.ai.AIChatActivity.class));
        });

        // Emergency hotlines direct dial
        binding.cardCallPolice.setOnClickListener(v -> dialNumber("117"));
        binding.cardCallMedical.setOnClickListener(v -> dialNumber("119"));
        binding.cardCallFire.setOnClickListener(v -> dialNumber("118"));
    }

    private void dialNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void observeViewModel() {
        homeViewModel.getCategoriesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                categoryAdapter.submitList(resource.getData());
            }
        });

        homeViewModel.getProvincesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                provinceAdapter.submitList(resource.getData());
            }
        });

        homeViewModel.getFeaturedPlacesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                featuredPlaceAdapter.setPlaces(resource.getData());
            }
        });

        homeViewModel.getPopularPlacesState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                popularPlaceAdapter.setPlaces(resource.getData());
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
