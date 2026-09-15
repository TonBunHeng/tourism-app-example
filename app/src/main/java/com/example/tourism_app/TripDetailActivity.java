package com.example.tourism_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourism_app.adapter.PlaceAdapter;
import com.example.tourism_app.databinding.ActivityTripDetailBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.Place;
import com.example.tourism_app.model.Trip;

public class TripDetailActivity extends AppCompatActivity {

    private ActivityTripDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Trip trip = (Trip) getIntent().getSerializableExtra("extra_trip");
        if (trip == null) {
            trip = MockDataProvider.getTrips().get(0);
        }

        binding.tvTripDetailTitle.setText(trip.getTitle());
        binding.tvTripDetailDates.setText(trip.getDates() + " • " + trip.getDestination());
        binding.toolbarTripDetail.setNavigationOnClickListener(v -> finish());

        PlaceAdapter placeAdapter = new PlaceAdapter(MockDataProvider.getFeaturedPlaces(), new PlaceAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(Place place) {
                Intent intent = new Intent(TripDetailActivity.this, PlaceDetailActivity.class);
                intent.putExtra("extra_place", place);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Place place, int position) {
                // Favorite
            }
        });
        binding.rvTripPlaces.setAdapter(placeAdapter);
    }
}