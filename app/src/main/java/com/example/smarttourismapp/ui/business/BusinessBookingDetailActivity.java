package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Booking;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.Locale;

public class BusinessBookingDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvRef, tvServiceName, tvDate, tvTime, tvGuests, tvPrice;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerEmail, tvSpecialRequests;
    private Chip chipStatus;
    private MaterialButton btnDial, btnEmail, btnConfirm, btnReject, btnComplete;
    private LinearLayout layoutActions;

    private BusinessRepository repository;
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_booking_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new BusinessRepository(this);
        booking = (Booking) getIntent().getSerializableExtra("booking");

        initViews();
        displayBookingDetails();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarBookingDetail);
        tvRef = findViewById(R.id.tvDetailBookingRef);
        chipStatus = findViewById(R.id.chipDetailStatus);
        tvServiceName = findViewById(R.id.tvDetailServiceName);
        tvDate = findViewById(R.id.tvDetailDate);
        tvTime = findViewById(R.id.tvDetailTime);
        tvGuests = findViewById(R.id.tvDetailGuests);
        tvPrice = findViewById(R.id.tvDetailPrice);

        tvCustomerName = findViewById(R.id.tvDetailCustomerName);
        tvCustomerPhone = findViewById(R.id.tvDetailCustomerPhone);
        tvCustomerEmail = findViewById(R.id.tvDetailCustomerEmail);
        tvSpecialRequests = findViewById(R.id.tvDetailSpecialRequests);

        btnDial = findViewById(R.id.btnDialCustomer);
        btnEmail = findViewById(R.id.btnEmailCustomer);
        btnConfirm = findViewById(R.id.btnDetailConfirm);
        btnReject = findViewById(R.id.btnDetailReject);
        btnComplete = findViewById(R.id.btnDetailComplete);
        layoutActions = findViewById(R.id.layoutDetailActionButtons);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayBookingDetails() {
        if (booking == null) return;

        tvRef.setText(booking.getBookingReference());
        tvServiceName.setText(booking.getTitle());
        tvDate.setText("📅 Date: " + booking.getBookingDate());
        tvTime.setText("🕒 Time: " + booking.getBookingTime());
        tvGuests.setText(String.format(Locale.US, "👥 Guests: %d People", booking.getNumberOfGuests()));
        tvPrice.setText(String.format(Locale.US, "💰 Total Price: $%.2f (%s)", booking.getTotalPrice(), booking.getPaymentStatus()));

        tvCustomerName.setText(booking.getCustomerName());
        tvCustomerPhone.setText(booking.getCustomerPhone().isEmpty() ? "No phone provided" : booking.getCustomerPhone());
        tvCustomerEmail.setText(booking.getCustomerEmail().isEmpty() ? "No email provided" : booking.getCustomerEmail());

        String requests = booking.getSpecialRequests();
        if (requests != null && !requests.trim().isEmpty()) {
            tvSpecialRequests.setText(requests);
        } else {
            tvSpecialRequests.setText("None specified by customer.");
        }

        updateStatusUI(booking.getStatus());
    }

    private void updateStatusUI(String status) {
        String s = (status != null ? status : "Pending").toLowerCase();

        btnConfirm.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);

        if ("pending".equals(s)) {
            chipStatus.setText("Pending Action");
            chipStatus.setChipBackgroundColorResource(R.color.warning);
            btnConfirm.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
        } else if ("confirmed".equals(s)) {
            chipStatus.setText("Confirmed");
            chipStatus.setChipBackgroundColorResource(R.color.info);
            btnComplete.setVisibility(View.VISIBLE);
        } else if ("completed".equals(s)) {
            chipStatus.setText("Completed");
            chipStatus.setChipBackgroundColorResource(R.color.success);
            layoutActions.setVisibility(View.GONE);
        } else if ("cancelled".equals(s)) {
            chipStatus.setText("Cancelled");
            chipStatus.setChipBackgroundColorResource(R.color.error);
            layoutActions.setVisibility(View.GONE);
        } else if ("rejected".equals(s)) {
            chipStatus.setText("Declined");
            chipStatus.setChipBackgroundColorResource(R.color.error);
            layoutActions.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnDial.setOnClickListener(v -> {
            if (booking != null && !booking.getCustomerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + booking.getCustomerPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Customer phone number not available.", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmail.setOnClickListener(v -> {
            if (booking != null && !booking.getCustomerEmail().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + booking.getCustomerEmail()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reservation " + booking.getBookingReference());
                startActivity(Intent.createChooser(intent, "Email Customer"));
            } else {
                Toast.makeText(this, "Customer email not available.", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (booking == null) return;
            repository.confirmBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
                @Override
                public void onSuccess(Booking data) {
                    Toast.makeText(BusinessBookingDetailActivity.this, "Reservation confirmed! Notification sent to guest.", Toast.LENGTH_LONG).show();
                    booking.setStatus("confirmed");
                    updateStatusUI("confirmed");
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(BusinessBookingDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnReject.setOnClickListener(v -> {
            if (booking == null) return;
            final EditText input = new EditText(this);
            input.setText("Service capacity full");

            new AlertDialog.Builder(this)
                    .setTitle("Decline Booking")
                    .setMessage("Enter a reason for declining this reservation:")
                    .setView(input)
                    .setPositiveButton("Decline", (d, w) -> {
                        String reason = input.getText().toString().trim();
                        repository.rejectBooking(booking.getId(), reason, new BusinessRepository.RepositoryCallback<Booking>() {
                            @Override
                            public void onSuccess(Booking data) {
                                Toast.makeText(BusinessBookingDetailActivity.this, "Reservation declined.", Toast.LENGTH_SHORT).show();
                                booking.setStatus("rejected");
                                updateStatusUI("rejected");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(BusinessBookingDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnComplete.setOnClickListener(v -> {
            if (booking == null) return;
            repository.completeBooking(booking.getId(), new BusinessRepository.RepositoryCallback<Booking>() {
                @Override
                public void onSuccess(Booking data) {
                    Toast.makeText(BusinessBookingDetailActivity.this, "Booking marked completed.", Toast.LENGTH_SHORT).show();
                    booking.setStatus("completed");
                    updateStatusUI("completed");
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(BusinessBookingDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

