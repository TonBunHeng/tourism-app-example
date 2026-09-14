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
import com.example.tourism_app.adapter.TripAdapter;
import com.example.tourism_app.databinding.FragmentTripsBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.Trip;
import java.util.List;

public class TripsFragment extends Fragment {

    private FragmentTripsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarTrips, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        List<Trip> trips = MockDataProvider.getTrips();
        if (trips.isEmpty()) {
            binding.layoutEmptyTrips.setVisibility(View.VISIBLE);
            binding.rvTrips.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyTrips.setVisibility(View.GONE);
            binding.rvTrips.setVisibility(View.VISIBLE);

            TripAdapter adapter = new TripAdapter(trips, trip -> {
                Intent intent = new Intent(requireContext(), TripDetailActivity.class);
                intent.putExtra("extra_trip", trip);
                startActivity(intent);
            });
            binding.rvTrips.setAdapter(adapter);
        }

        binding.fabCreateTrip.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateTripActivity.class);
            startActivity(intent);
        });

        binding.swipeRefreshTrips.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding != null) {
                binding.swipeRefreshTrips.setRefreshing(false);
            }
        }, 1000));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}