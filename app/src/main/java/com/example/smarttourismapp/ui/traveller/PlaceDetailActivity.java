package com.example.smarttourismapp.ui.traveller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.databinding.ActivityPlaceDetailBinding;
import com.example.smarttourismapp.databinding.DialogCreateBookingBinding;
import com.example.smarttourismapp.databinding.DialogWriteReviewBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Review;
import com.example.smarttourismapp.model.ReviewRequest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Place Detail Screen matching DestinationDetails.jsx in tourism-travel.
 * Features:
 * - Cover gallery photo & destination specs (Entry fee, best season, duration)
 * - Map integration (Google Maps intent for latitude/longitude & address)
 * - Review list, rating stats, and review submission dialog
 * - Guided visit / booking reservation dialog
 * - Wishlist favorite toggle & system share intent
 */
public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE = "extra_place";
    public static final String EXTRA_PLACE_ID = "extra_place_id";

    private ActivityPlaceDetailBinding binding;
    private TravelRepository travelRepository;
    private ApiService apiService;
    private ReviewAdapter reviewAdapter;

    private Place currentPlace;
    private boolean isFavorited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        travelRepository = new TravelRepository(this);
        apiService = ApiClient.getApiService(this);

        setupRecyclerView();
        setupListeners();
        extractIntentData();
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter();
        binding.rvPlaceReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlaceReviews.setAdapter(reviewAdapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnShare.setOnClickListener(v -> shareDestination());
        binding.btnFavorite.setOnClickListener(v -> toggleFavorite());
        binding.btnViewOnMap.setOnClickListener(v -> openMapLocation());
        binding.btnWriteReview.setOnClickListener(v -> showWriteReviewDialog());
        binding.btnBook.setOnClickListener(v -> showBookingDialog());
    }

    private void extractIntentData() {
        if (getIntent().hasExtra(EXTRA_PLACE)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                currentPlace = getIntent().getSerializableExtra(EXTRA_PLACE, Place.class);
            } else {
                currentPlace = (Place) getIntent().getSerializableExtra(EXTRA_PLACE);
            }
            displayPlaceDetails(currentPlace);
        }

        long targetPlaceId = getIntent().getLongExtra(EXTRA_PLACE_ID, -1);
        if (currentPlace != null && currentPlace.getId() != null) {
            targetPlaceId = currentPlace.getId();
        }

        if (targetPlaceId != -1) {
            fetchLatestDetails(targetPlaceId);
            fetchReviews(targetPlaceId);
        }
    }

    private void displayPlaceDetails(Place place) {
        if (place == null) return;
        this.currentPlace = place;

        binding.tvPlaceName.setText(place.getName());

        String category = place.getCategoryName();
        if (TextUtils.isEmpty(category)) category = "Destination";
        binding.tvCategory.setText(category);

        String province = place.getProvinceName();
        if (TextUtils.isEmpty(province)) province = "Cambodia";
        binding.tvLocation.setText(province);

        float rating = place.getRating() > 0 ? place.getRating() : 4.8f;
        int reviews = place.getReviewsCount() > 0 ? place.getReviewsCount() : 12;
        binding.tvRating.setText(String.format(Locale.US, "%.1f (%d reviews)", rating, reviews));

        if (!TextUtils.isEmpty(place.getPrice())) {
            binding.tvPrice.setText(place.getPrice());
        } else {
            binding.tvPrice.setText("Free Entry");
        }

        if (!TextUtils.isEmpty(place.getBestTime())) {
            binding.tvBestTime.setText(place.getBestTime());
        } else {
            binding.tvBestTime.setText("Nov - Feb");
        }

        if (!TextUtils.isEmpty(place.getDuration())) {
            binding.tvDuration.setText(place.getDuration());
        } else {
            binding.tvDuration.setText("Full Day");
        }

        if (!TextUtils.isEmpty(place.getDescription())) {
            binding.tvDescription.setText(place.getDescription());
        } else {
            binding.tvDescription.setText("Explore this historic Cambodian attraction. Experience authentic heritage and scenic beauty.");
        }

        if (!TextUtils.isEmpty(place.getAddress())) {
            binding.tvAddress.setText(place.getAddress());
        } else {
            binding.tvAddress.setText(province + ", Cambodia");
        }

        if (!TextUtils.isEmpty(place.getImageUrl())) {
            Glide.with(this)
                    .load(place.getImageUrl())
                    .placeholder(R.drawable.onboarding_1)
                    .error(R.drawable.onboarding_1)
                    .into(binding.ivPlaceCover);
        } else {
            binding.ivPlaceCover.setImageResource(R.drawable.onboarding_1);
        }
    }

    private void fetchLatestDetails(long placeId) {
        travelRepository.getPlaceDetails(placeId, new TravelRepository.RepositoryCallback<Place>() {
            @Override
            public void onSuccess(Place place) {
                if (place != null) {
                    displayPlaceDetails(place);
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void fetchReviews(long placeId) {
        apiService.getReviews(placeId, 1).enqueue(new Callback<ApiResponse<List<Review>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Review>>> call, Response<ApiResponse<List<Review>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Review> reviews = response.body().getData();
                    reviewAdapter.submitList(reviews);
                    if (reviews.isEmpty()) {
                        binding.tvNoReviews.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvNoReviews.setVisibility(View.GONE);
                    }
                } else {
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Review>>> call, Throwable t) {
                binding.tvNoReviews.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openMapLocation() {
        if (currentPlace == null) return;
        String query;
        if (currentPlace.getLatitude() != null && currentPlace.getLongitude() != null) {
            query = currentPlace.getLatitude() + "," + currentPlace.getLongitude() + "(" + currentPlace.getName() + ")";
        } else {
            query = currentPlace.getName() + ", " + currentPlace.getProvinceName() + ", Cambodia";
        }
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query));
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    private void shareDestination() {
        if (currentPlace == null) return;
        String shareText = "Check out " + currentPlace.getName() + " in " + currentPlace.getProvinceName() + ", Cambodia!\n" +
                "Plan your trip with the Smart Tourism App.";
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Destination"));
    }

    private void toggleFavorite() {
        if (currentPlace == null || currentPlace.getId() == null) return;
        isFavorited = !isFavorited;
        binding.btnFavorite.setAlpha(isFavorited ? 1.0f : 0.6f);

        travelRepository.toggleFavorite(currentPlace.getId(), new TravelRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(PlaceDetailActivity.this, isFavorited ? "Saved to wishlist!" : "Removed from wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PlaceDetailActivity.this, errorMessage != null ? errorMessage : "Could not update wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWriteReviewDialog() {
        if (currentPlace == null || currentPlace.getId() == null) return;

        DialogWriteReviewBinding dialogBinding = DialogWriteReviewBinding.inflate(LayoutInflater.from(this));

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Submit Review", (dialog, which) -> {
                    int rating = (int) dialogBinding.ratingBar.getRating();
                    String title = dialogBinding.etReviewTitle.getText() != null ? dialogBinding.etReviewTitle.getText().toString().trim() : "";
                    String comment = dialogBinding.etReviewComment.getText() != null ? dialogBinding.etReviewComment.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(comment)) {
                        Toast.makeText(this, "Please write your review feedback.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ReviewRequest request = new ReviewRequest(currentPlace.getId(), rating > 0 ? rating : 5, title, comment);
                    apiService.createReview(request).enqueue(new Callback<ApiResponse<Review>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Review>> call, Response<ApiResponse<Review>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PlaceDetailActivity.this, "Thank you! Your review has been submitted.", Toast.LENGTH_SHORT).show();
                                fetchReviews(currentPlace.getId());
                            } else {
                                Toast.makeText(PlaceDetailActivity.this, ApiErrorParser.parseMessage(response), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                            Toast.makeText(PlaceDetailActivity.this, ApiErrorParser.parseThrowable(t), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showBookingDialog() {
        if (currentPlace == null || currentPlace.getId() == null) return;

        DialogCreateBookingBinding dialogBinding = DialogCreateBookingBinding.inflate(LayoutInflater.from(this));
        dialogBinding.tvBookingDialogTitle.setText("Visit: " + currentPlace.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String defaultDate = String.format(Locale.US, "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        dialogBinding.etBookingDate.setText(defaultDate);

        dialogBinding.etBookingDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                dialogBinding.etBookingDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Confirm Reservation", (dialog, which) -> {
                    String date = dialogBinding.etBookingDate.getText() != null ? dialogBinding.etBookingDate.getText().toString().trim() : "";
                    String time = dialogBinding.etBookingTime.getText() != null ? dialogBinding.etBookingTime.getText().toString().trim() : "09:00 AM";
                    String guestsStr = dialogBinding.etBookingGuests.getText() != null ? dialogBinding.etBookingGuests.getText().toString().trim() : "2";
                    int guests = 2;
                    try {
                        guests = Integer.parseInt(guestsStr);
                    } catch (NumberFormatException ignored) {}

                    String requests = dialogBinding.etBookingRequests.getText() != null ? dialogBinding.etBookingRequests.getText().toString().trim() : "";

                    Map<String, Object> bookingPayload = new HashMap<>();
                    bookingPayload.put("booking_date", date);
                    bookingPayload.put("booking_time", time);
                    bookingPayload.put("number_of_guests", guests);
                    bookingPayload.put("special_requests", requests);
                    bookingPayload.put("place_id", currentPlace.getId());

                    apiService.createBooking(bookingPayload).enqueue(new Callback<ApiResponse<Booking>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PlaceDetailActivity.this, "Reservation confirmed! You can view it in My Bookings.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PlaceDetailActivity.this, ApiErrorParser.parseMessage(response), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                            Toast.makeText(PlaceDetailActivity.this, ApiErrorParser.parseThrowable(t), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
