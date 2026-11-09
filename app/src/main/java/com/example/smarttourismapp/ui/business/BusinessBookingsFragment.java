package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.example.smarttourismapp.ui.business.adapter.BusinessBookingAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BusinessBookingsFragment extends Fragment {

    private TextInputEditText etSearch;
    private ChipGroup chipGroupStatus;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View emptyView;
    private ProgressBar progressBar;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private BusinessBookingAdapter adapter;

    private Long businessId;
    private String selectedStatus = null;
    private String searchQuery = "";
    private final List<Booking> allBookings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_bookings, container, false);
        initViews(view);
        setupAdapter();
        setupFilters();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new BusinessRepository(requireContext());
        appPreference = new AppPreference(requireContext());

        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;

        loadBookings();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearchBookings);
        chipGroupStatus = view.findViewById(R.id.chipGroupBookingStatus);
        swipeRefresh = view.findViewById(R.id.swipeRefreshBookings);
        recyclerView = view.findViewById(R.id.rvBookings);
        emptyView = view.findViewById(R.id.layoutEmptyBookings);
        progressBar = view.findViewById(R.id.pbLoadingBookings);
    }

    private void setupAdapter() {
        adapter = new BusinessBookingAdapter(requireContext(), new BusinessBookingAdapter.OnBookingActionListener() {
            @Override
            public void onConfirm(Booking booking) {
                confirmBooking(booking);
            }

            @Override
            public void onReject(Booking booking) {
                promptRejectReason(booking);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadBookings);
    }

    private void setupFilters() {
        chipGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFilterPending) {
                selectedStatus = "pending";
            } else if (checkedId == R.id.chipFilterConfirmed) {
                selectedStatus = "confirmed";
            } else if (checkedId == R.id.chipFilterCompleted) {
                selectedStatus = "completed";
            } else if (checkedId == R.id.chipFilterCancelled) {
                selectedStatus = "cancelled";
            } else {
                selectedStatus = null;
            }
            applyFilters();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s != null ? s.toString().trim().toLowerCase() : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        repository.getBookings(businessId, null, null, 1, new BusinessRepository.RepositoryCallback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> data) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                allBookings.clear();
                if (data != null) allBookings.addAll(data);
                applyFilters();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyFilters() {
        List<Booking> filtered = new ArrayList<>();
        for (Booking b : allBookings) {
            boolean matchesStatus = (selectedStatus == null) || selectedStatus.equalsIgnoreCase(b.getStatus());
            boolean matchesSearch = searchQuery.isEmpty()
                    || b.getCustomerName().toLowerCase().contains(searchQuery)
                    || b.getBookingReference().toLowerCase().contains(searchQuery)
                    || b.getServiceName().toLowerCase().contains(searchQuery);

            if (matchesStatus && matchesSearch) {
                filtered.add(b);
            }
        }

        adapter.setBookings(filtered);
        if (filtered.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void confirmBooking(Booking booking) {
        repository.confirmBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
            @Override
            public void onSuccess(Booking data) {
                Toast.makeText(requireContext(), "Reservation confirmed! Guest notified.", Toast.LENGTH_SHORT).show();
                loadBookings();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptRejectReason(Booking booking) {
        final EditText input = new EditText(requireContext());
        input.setHint("e.g. Service capacity full");
        input.setText("Service capacity full");

        new AlertDialog.Builder(requireContext())
                .setTitle("Decline Booking")
                .setMessage("Enter a reason for declining this reservation:")
                .setView(input)
                .setPositiveButton("Decline", (d, w) -> {
                    String reason = input.getText().toString().trim();
                    repository.rejectBooking(booking.getId(), reason, new BusinessRepository.RepositoryCallback<Booking>() {
                        @Override
                        public void onSuccess(Booking data) {
                            Toast.makeText(requireContext(), "Reservation declined.", Toast.LENGTH_SHORT).show();
                            loadBookings();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void completeBooking(Booking booking) {
        repository.completeBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
            @Override
            public void onSuccess(Booking data) {
                Toast.makeText(requireContext(), "Booking marked completed.", Toast.LENGTH_SHORT).show();
                loadBookings();
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

