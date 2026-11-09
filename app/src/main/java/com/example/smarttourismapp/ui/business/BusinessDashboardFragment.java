package com.example.smarttourismapp.ui.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.BusinessBookingStats;
import com.example.smarttourismapp.model.Review;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.business.adapter.BusinessBookingAdapter;
import com.example.smarttourismapp.ui.business.adapter.BusinessReviewAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Locale;

public class BusinessDashboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvOwnerGreeting, tvActiveBusinessName, tvActiveBusinessMeta;
    private Chip chipVerification;
    private MaterialCardView cardPendingNotice;
    private MaterialButton btnSwitchBusiness, btnEditProfileShortcut;

    private TextView tvKpiTotalBookings, tvKpiBookingsSub, tvKpiPendingActions, tvKpiRevenue, tvKpiRating;

    private View shortcutBookings, shortcutListings, shortcutHours, shortcutReviews, shortcutReports, shortcutProfile;
    private TextView tvViewAllBookings, tvViewAllReviews;
    private RecyclerView rvRecentBookings, rvRecentReviews;
    private TextView tvEmptyBookings, tvEmptyReviews;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private BusinessBookingAdapter bookingAdapter;
    private BusinessReviewAdapter reviewAdapter;

    private Business activeBusiness;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initViews(view);
        setupAdapters();
        setupListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new BusinessRepository(requireContext());
        appPreference = new AppPreference(requireContext());
        loadDashboard();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshDashboard);
        tvOwnerGreeting = view.findViewById(R.id.tvOwnerGreeting);
        tvActiveBusinessName = view.findViewById(R.id.tvActiveBusinessName);
        tvActiveBusinessMeta = view.findViewById(R.id.tvActiveBusinessMeta);
        chipVerification = view.findViewById(R.id.chipVerification);
        cardPendingNotice = view.findViewById(R.id.cardPendingNotice);
        btnSwitchBusiness = view.findViewById(R.id.btnSwitchBusiness);
        btnEditProfileShortcut = view.findViewById(R.id.btnEditProfileShortcut);

        tvKpiTotalBookings = view.findViewById(R.id.tvKpiTotalBookings);
        tvKpiBookingsSub = view.findViewById(R.id.tvKpiBookingsSub);
        tvKpiPendingActions = view.findViewById(R.id.tvKpiPendingActions);
        tvKpiRevenue = view.findViewById(R.id.tvKpiRevenue);
        tvKpiRating = view.findViewById(R.id.tvKpiRating);

        shortcutBookings = view.findViewById(R.id.shortcutBookings);
        shortcutListings = view.findViewById(R.id.shortcutListings);
        shortcutHours = view.findViewById(R.id.shortcutHours);
        shortcutReviews = view.findViewById(R.id.shortcutReviews);
        shortcutReports = view.findViewById(R.id.shortcutReports);
        shortcutProfile = view.findViewById(R.id.shortcutProfile);

        tvViewAllBookings = view.findViewById(R.id.tvViewAllBookings);
        tvViewAllReviews = view.findViewById(R.id.tvViewAllReviews);

        rvRecentBookings = view.findViewById(R.id.rvRecentBookings);
        rvRecentReviews = view.findViewById(R.id.rvRecentReviews);

        tvEmptyBookings = view.findViewById(R.id.tvEmptyBookings);
        tvEmptyReviews = view.findViewById(R.id.tvEmptyReviews);
    }

    private void setupAdapters() {
        bookingAdapter = new BusinessBookingAdapter(requireContext(), new BusinessBookingAdapter.OnBookingActionListener() {
            @Override
            public void onConfirm(Booking booking) {
                confirmBooking(booking);
            }

            @Override
            public void onReject(Booking booking) {
                rejectBooking(booking);
            }

            @Override
            public void onComplete(Booking booking) {
                completeBooking(booking);
            }

            @Override
            public void onDetails(Booking booking) {
                openBookingDetail(booking);
            }
        });
        rvRecentBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentBookings.setAdapter(bookingAdapter);

        reviewAdapter = new BusinessReviewAdapter(requireContext(), review -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_reviews);
            }
        });
        rvRecentReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentReviews.setAdapter(reviewAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadDashboard);

        btnSwitchBusiness.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).showBusinessSwitchDialog();
            }
        });

        btnEditProfileShortcut.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BusinessEditProfileActivity.class);
            startActivity(intent);
        });

        shortcutBookings.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_bookings);
            }
        });

        shortcutListings.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_listings);
            }
        });

        shortcutHours.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BusinessHoursActivity.class);
            startActivity(intent);
        });

        shortcutReviews.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_reviews);
            }
        });

        shortcutReports.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BusinessReportsActivity.class);
            startActivity(intent);
        });

        shortcutProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BusinessEditProfileActivity.class);
            startActivity(intent);
        });

        tvViewAllBookings.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_bookings);
            }
        });

        tvViewAllReviews.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessMainActivity) {
                ((BusinessMainActivity) getActivity()).navigateToTab(R.id.nav_business_reviews);
            }
        });
    }

    public void loadDashboard() {
        if (!isAdded()) return;

        User user = appPreference.getUser();
        if (user != null) {
            tvOwnerGreeting.setText("Welcome back, " + user.getName());
        }

        activeBusiness = appPreference.getSelectedBusiness();
        if (activeBusiness != null) {
            updateBusinessHeader(activeBusiness);
        }

        Long bizId = activeBusiness != null ? activeBusiness.getId() : null;

        // 1. Fetch Stats
        repository.getBookingStatistics(bizId, new BusinessRepository.RepositoryCallback<BusinessBookingStats>() {
            @Override
            public void onSuccess(BusinessBookingStats stats) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                if (stats != null) {
                    tvKpiTotalBookings.setText(String.valueOf(stats.getTotalBookings()));
                    tvKpiBookingsSub.setText(String.format(Locale.US, "%d confirmed • %d pending",
                            stats.getConfirmedBookings(), stats.getPendingBookings()));
                    tvKpiPendingActions.setText(String.valueOf(stats.getPendingBookings()));
                    tvKpiRevenue.setText(String.format(Locale.US, "$%.2f", stats.getTotalRevenue()));
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
            }
        });

        // 2. Fetch Recent Bookings
        repository.getBookings(bizId, null, null, 1, new BusinessRepository.RepositoryCallback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> data) {
                if (!isAdded()) return;
                if (data != null && !data.isEmpty()) {
                    List<Booking> top5 = data.size() > 5 ? data.subList(0, 5) : data;
                    bookingAdapter.setBookings(top5);
                    rvRecentBookings.setVisibility(View.VISIBLE);
                    tvEmptyBookings.setVisibility(View.GONE);
                } else {
                    bookingAdapter.setBookings(null);
                    rvRecentBookings.setVisibility(View.GONE);
                    tvEmptyBookings.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                tvEmptyBookings.setVisibility(View.VISIBLE);
            }
        });

        // 3. Fetch Recent Reviews
        if (bizId != null) {
            repository.getReviews(bizId, new BusinessRepository.RepositoryCallback<List<Review>>() {
                @Override
                public void onSuccess(List<Review> data) {
                    if (!isAdded()) return;
                    if (data != null && !data.isEmpty()) {
                        List<Review> top3 = data.size() > 3 ? data.subList(0, 3) : data;
                        reviewAdapter.setReviews(top3);
                        rvRecentReviews.setVisibility(View.VISIBLE);
                        tvEmptyReviews.setVisibility(View.GONE);

                        float total = 0;
                        for (Review r : data) total += r.getRating();
                        float avg = total / data.size();
                        tvKpiRating.setText(String.format(Locale.US, "%.1f ★", avg));
                    } else {
                        reviewAdapter.setReviews(null);
                        rvRecentReviews.setVisibility(View.GONE);
                        tvEmptyReviews.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    tvEmptyReviews.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void updateBusinessHeader(Business biz) {
        tvActiveBusinessName.setText(biz.getName());
        tvActiveBusinessMeta.setText(biz.getCategory() + " • " + biz.getProvince());

        if (biz.isVerified()) {
            chipVerification.setText("Verified");
            chipVerification.setChipBackgroundColorResource(R.color.success);
            cardPendingNotice.setVisibility(View.GONE);
        } else {
            chipVerification.setText("Pending Verification");
            chipVerification.setChipBackgroundColorResource(R.color.warning);
            cardPendingNotice.setVisibility(View.VISIBLE);
        }
    }

    private void confirmBooking(Booking booking) {
        repository.confirmBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
            @Override
            public void onSuccess(Booking data) {
                Toast.makeText(requireContext(), "Reservation confirmed! Guest notified.", Toast.LENGTH_SHORT).show();
                loadDashboard();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectBooking(Booking booking) {
        repository.rejectBooking(booking.getId(), "Capacity full", new BusinessRepository.RepositoryCallback<Booking>() {
            @Override
            public void onSuccess(Booking data) {
                Toast.makeText(requireContext(), "Reservation declined.", Toast.LENGTH_SHORT).show();
                loadDashboard();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeBooking(Booking booking) {
        repository.completeBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
            @Override
            public void onSuccess(Booking data) {
                Toast.makeText(requireContext(), "Booking marked completed.", Toast.LENGTH_SHORT).show();
                loadDashboard();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openBookingDetail(Booking booking) {
        Intent intent = new Intent(requireContext(), BusinessBookingDetailActivity.class);
        intent.putExtra("booking", booking);
        startActivity(intent);
    }
}

