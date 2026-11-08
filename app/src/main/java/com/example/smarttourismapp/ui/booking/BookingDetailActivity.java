package com.example.smarttourismapp.ui.booking;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ActivityBookingDetailBinding;
import com.example.smarttourismapp.model.Booking;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING = "extra_booking";

    private ActivityBookingDetailBinding binding;
    private BookingsViewModel bookingsViewModel;
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bookingsViewModel = new ViewModelProvider(this).get(BookingsViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        booking = (Booking) getIntent().getSerializableExtra(EXTRA_BOOKING);
        if (booking == null) {
            Toast.makeText(this, "Booking details unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBooking();
        setupCancelAction();
        observeCancelState();
    }

    private void displayBooking() {
        binding.tvBookingRef.setText(booking.getBookingReference());
        binding.tvTargetName.setText(booking.getTitle());

        if (booking.getBusiness() != null && !TextUtils.isEmpty(booking.getBusiness().getAddress())) {
            binding.tvTargetLocation.setText(booking.getBusiness().getAddress());
            binding.tvTargetLocation.setVisibility(View.VISIBLE);
        } else {
            binding.tvTargetLocation.setVisibility(View.GONE);
        }

        binding.tvBookingDate.setText(booking.getBookingDate());
        binding.tvBookingTime.setText(
                !TextUtils.isEmpty(booking.getBookingTime()) ? booking.getBookingTime() : "Flexible / Any time");
        binding.tvBookingGuests.setText(booking.getNumberOfGuests() + " Guests");

        if (booking.getTotalPrice() != null && booking.getTotalPrice() > 0) {
            binding.tvBookingPrice.setText(String.format("$%.2f", booking.getTotalPrice()));
        } else {
            binding.tvBookingPrice.setText("Free / Included");
        }

        if (!TextUtils.isEmpty(booking.getSpecialRequests())) {
            binding.tvSpecialRequests.setText(booking.getSpecialRequests());
            binding.tvSpecialRequests.setVisibility(View.VISIBLE);
        } else {
            binding.tvSpecialRequests.setText("None specified");
        }

        // Status badge styling
        String status = booking.getStatus();
        binding.tvBookingStatus.setText(status.toUpperCase());

        if ("confirmed".equalsIgnoreCase(status)) {
            binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
            binding.tvBookingStatus.setTextColor(Color.parseColor("#2E7D32"));
            binding.btnCancelReservation.setVisibility(View.VISIBLE);
        } else if ("pending".equalsIgnoreCase(status)) {
            binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
            binding.tvBookingStatus.setTextColor(Color.parseColor("#EF6C00"));
            binding.btnCancelReservation.setVisibility(View.VISIBLE);
        } else if ("completed".equalsIgnoreCase(status)) {
            binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
            binding.tvBookingStatus.setTextColor(Color.parseColor("#1565C0"));
            binding.btnCancelReservation.setVisibility(View.GONE);
        } else {
            binding.tvBookingStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
            binding.tvBookingStatus.setTextColor(Color.parseColor("#C62828"));
            binding.btnCancelReservation.setVisibility(View.GONE);
        }
    }

    private void setupCancelAction() {
        binding.btnCancelReservation.setOnClickListener(v -> {
            EditText etReason = new EditText(this);
            etReason.setHint("Reason for cancellation (optional)");

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Cancel Reservation")
                    .setMessage("Are you sure you want to cancel this booking? Please provide an optional reason:")
                    .setView(etReason)
                    .setPositiveButton("Confirm Cancellation", (dialog, which) -> {
                        String reason = etReason.getText() != null ? etReason.getText().toString().trim()
                                : "Cancelled by user";
                        bookingsViewModel.cancelBooking(booking.getId(), reason);
                    })
                    .setNegativeButton("Keep Booking", null)
                    .show();
        });
    }

    private void observeCancelState() {
        bookingsViewModel.getCancelState().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.getStatus()) {
                case LOADING:
                    binding.btnCancelReservation.setEnabled(false);
                    binding.btnCancelReservation.setText("Cancelling...");
                    break;
                case SUCCESS:
                    Toast.makeText(this, "Reservation cancelled successfully.", Toast.LENGTH_SHORT).show();
                    booking.setStatus("cancelled");
                    displayBooking();
                    break;
                case ERROR:
                    binding.btnCancelReservation.setEnabled(true);
                    binding.btnCancelReservation.setText("Cancel Reservation");
                    Toast.makeText(this, resource.getMessage() != null ? resource.getMessage() : "Failed to cancel",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}
