package com.example.smarttourismapp.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.databinding.FragmentBookingsBinding;
import com.example.smarttourismapp.model.Booking;

import java.util.List;

public class BookingsFragment extends Fragment implements BookingAdapter.OnBookingClickListener {

    private FragmentBookingsBinding binding;
    private BookingsViewModel bookingsViewModel;
    private BookingAdapter bookingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingsViewModel = new ViewModelProvider(this).get(BookingsViewModel.class);

        setupRecyclerView();
        setupStatusChips();
        observeViewModel();

        bookingsViewModel.loadBookings();
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this);
        binding.rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookings.setAdapter(bookingAdapter);

        binding.swipeRefreshBookings.setOnRefreshListener(() -> {
            bookingsViewModel.loadBookings();
            binding.swipeRefreshBookings.setRefreshing(false);
        });
    }

    private void setupStatusChips() {
        binding.chipBookingAll.setOnClickListener(v -> bookingsViewModel.setStatusFilter("all"));
        binding.chipBookingPending.setOnClickListener(v -> bookingsViewModel.setStatusFilter("pending"));
        binding.chipBookingConfirmed.setOnClickListener(v -> bookingsViewModel.setStatusFilter("confirmed"));
        binding.chipBookingCompleted.setOnClickListener(v -> bookingsViewModel.setStatusFilter("completed"));
        binding.chipBookingCancelled.setOnClickListener(v -> bookingsViewModel.setStatusFilter("cancelled"));
    }

    private void observeViewModel() {
        bookingsViewModel.getBookingsState().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null)
                return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.loadingBookings.setVisibility(View.VISIBLE);
                    binding.layoutBookingsEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.loadingBookings.setVisibility(View.GONE);
                    List<Booking> list = resource.getData();
                    bookingAdapter.submitList(list);
                    if (list == null || list.isEmpty()) {
                        binding.layoutBookingsEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutBookingsEmpty.setVisibility(View.GONE);
                    }
                    break;
                case ERROR:
                    binding.loadingBookings.setVisibility(View.GONE);
                    binding.layoutBookingsEmpty.setVisibility(View.VISIBLE);
                    break;
            }
        });

        bookingsViewModel.getCancelState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(requireContext(), "Reservation has been cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBookingClick(Booking booking) {
        if (booking == null)
            return;
        android.content.Intent intent = new android.content.Intent(requireContext(), BookingDetailActivity.class);
        intent.putExtra(BookingDetailActivity.EXTRA_BOOKING, booking);
        startActivity(intent);
    }

    @Override
    public void onCancelBooking(Booking booking) {
        if (booking == null || booking.getId() == null)
            return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this booking (" + booking.getBookingReference() + ")?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    bookingsViewModel.cancelBooking(booking.getId(), "Cancelled by traveler");
                })
                .setNegativeButton("Keep Reservation", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
