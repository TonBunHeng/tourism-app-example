package com.example.tourism_app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourism_app.adapter.ReviewAdapter;
import com.example.tourism_app.databinding.ActivityPlaceDetailBinding;
import com.example.tourism_app.model.MockDataProvider;
import com.example.tourism_app.model.Place;

public class PlaceDetailActivity extends AppCompatActivity {

    private ActivityPlaceDetailBinding binding;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        place = (Place) getIntent().getSerializableExtra("extra_place");
        if (place == null) {
            place = MockDataProvider.getFeaturedPlaces().get(0);
        }

        binding.tvDetailTitle.setText(place.getName());
        binding.tvDetailLocation.setText(place.getLocation());
        binding.tvDetailRating.setText(place.getRating() + " (1.2k reviews)");
        binding.tvDetailDescription.setText(place.getDescription());
        binding.tvDetailPrice.setText(place.getPrice());
        binding.ivDetailImage.setImageResource(place.getImageResId());

        if (place.isFavorite()) {
            binding.btnDetailFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            binding.btnDetailFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

        binding.btnDetailFavorite.setOnClickListener(v -> {
            place.setFavorite(!place.isFavorite());
            if (place.isFavorite()) {
                binding.btnDetailFavorite.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(this, "Saved to Favorites", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnDetailFavorite.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDetailBack.setOnClickListener(v -> finish());

        ReviewAdapter reviewAdapter = new ReviewAdapter(MockDataProvider.getReviews());
        binding.rvDetailReviews.setAdapter(reviewAdapter);

        binding.btnBookNow.setOnClickListener(v ->
                Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show()
        );

        binding.btnAddToTrip.setOnClickListener(v ->
                Toast.makeText(this, "Added to Trip Itinerary!", Toast.LENGTH_SHORT).show()
        );
    }
}