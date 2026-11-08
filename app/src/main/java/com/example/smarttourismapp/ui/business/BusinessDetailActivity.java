package com.example.smarttourismapp.ui.business;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.databinding.ActivityBusinessDetailBinding;
import com.example.smarttourismapp.databinding.DialogCreateBookingBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Business;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BUSINESS = "extra_business";
    public static final String EXTRA_BUSINESS_ID = "extra_business_id";

    private ActivityBusinessDetailBinding binding;
    private ApiService apiService;
    private Business currentBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBusinessDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        apiService = ApiClient.getApiService(this);

        setupListeners();
        extractIntentData();
    }

    private void setupListeners() {
        binding.btnBusinessDetailBack.setOnClickListener(v -> finish());

        binding.layoutBusinessPhone.setOnClickListener(v -> {
            if (currentBusiness != null && !TextUtils.isEmpty(currentBusiness.getPhone())) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentBusiness.getPhone()));
                startActivity(intent);
            }
        });

        binding.btnBusinessBookNow.setOnClickListener(v -> showBookingDialog());
    }

    private void extractIntentData() {
        if (getIntent().hasExtra(EXTRA_BUSINESS)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                currentBusiness = getIntent().getSerializableExtra(EXTRA_BUSINESS, Business.class);
            } else {
                currentBusiness = (Business) getIntent().getSerializableExtra(EXTRA_BUSINESS);
            }
            displayBusinessDetails(currentBusiness);
        }

        long targetId = getIntent().getLongExtra(EXTRA_BUSINESS_ID, -1);
        if (currentBusiness != null && currentBusiness.getId() != null) {
            targetId = currentBusiness.getId();
        }

        if (targetId != -1) {
            loadServices(targetId);
        }
    }

    private void displayBusinessDetails(Business b) {
        if (b == null)
            return;
        this.currentBusiness = b;

        binding.tvBusinessDetailName.setText(b.getName());
        binding.tvBusinessDetailCategory.setText(b.getCategoryString());
        binding.tvBusinessDetailAddress.setText(b.getAddress().isEmpty() ? b.getProvinceString() : b.getAddress());

        if (!TextUtils.isEmpty(b.getPhone())) {
            binding.tvBusinessDetailPhone.setText(b.getPhone());
            binding.layoutBusinessPhone.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBusinessPhone.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(b.getDescription())) {
            binding.tvBusinessDetailDescription.setText(b.getDescription());
        }

        if (!TextUtils.isEmpty(b.getImageUrl())) {
            Glide.with(this)
                    .load(b.getImageUrl())
                    .placeholder(R.drawable.onboarding_2)
                    .error(R.drawable.onboarding_2)
                    .into(binding.ivBusinessDetailCover);
        } else {
            binding.ivBusinessDetailCover.setImageResource(R.drawable.onboarding_2);
        }
    }

    private void loadServices(long businessId) {
        apiService.getBusinessServices(businessId).enqueue(new Callback<ApiResponse<List<Map<String, Object>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Map<String, Object>>>> call,
                    Response<ApiResponse<List<Map<String, Object>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Map<String, Object>> services = response.body().getData();
                    populateServices(services);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Map<String, Object>>>> call, Throwable t) {
            }
        });
    }

    private void populateServices(List<Map<String, Object>> services) {
        binding.layoutServicesContainer.removeAllViews();
        if (services == null || services.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No specific services listed. You can make a direct visit reservation below.");
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setTextSize(13);
            binding.layoutServicesContainer.addView(empty);
            return;
        }

        for (Map<String, Object> service : services) {
            String name = String.valueOf(service.get("name") != null ? service.get("name") : "Service Package");
            String price = String.valueOf(service.get("price") != null ? "$" + service.get("price") : "Inquire");

            TextView tv = new TextView(this);
            tv.setText("• " + name + " — " + price);
            tv.setTextColor(getColor(R.color.text_primary));
            tv.setTextSize(14);
            tv.setPadding(0, 8, 0, 8);
            binding.layoutServicesContainer.addView(tv);
        }
    }

    private void showBookingDialog() {
        if (currentBusiness == null || currentBusiness.getId() == null)
            return;

        DialogCreateBookingBinding dialogBinding = DialogCreateBookingBinding.inflate(LayoutInflater.from(this));
        dialogBinding.tvBookingDialogTitle.setText("Reserve at " + currentBusiness.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String defaultDate = String.format(Locale.US, "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        dialogBinding.etBookingDate.setText(defaultDate);

        dialogBinding.etBookingDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                dialogBinding.etBookingDate
                        .setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Confirm Reservation", (dialog, which) -> {
                    String date = dialogBinding.etBookingDate.getText() != null
                            ? dialogBinding.etBookingDate.getText().toString().trim()
                            : "";
                    String time = dialogBinding.etBookingTime.getText() != null
                            ? dialogBinding.etBookingTime.getText().toString().trim()
                            : "10:00 AM";
                    String guestsStr = dialogBinding.etBookingGuests.getText() != null
                            ? dialogBinding.etBookingGuests.getText().toString().trim()
                            : "2";
                    int guests = 2;
                    try {
                        guests = Integer.parseInt(guestsStr);
                    } catch (NumberFormatException ignored) {
                    }

                    String requests = dialogBinding.etBookingRequests.getText() != null
                            ? dialogBinding.etBookingRequests.getText().toString().trim()
                            : "";

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("booking_date", date);
                    payload.put("booking_time", time);
                    payload.put("number_of_guests", guests);
                    payload.put("special_requests", requests);

                    apiService.createBusinessBooking(currentBusiness.getId(), payload)
                            .enqueue(new Callback<ApiResponse<Booking>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Booking>> call,
                                        Response<ApiResponse<Booking>> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(BusinessDetailActivity.this,
                                                "Reservation confirmed! You can view it in My Bookings.",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(BusinessDetailActivity.this,
                                                ApiErrorParser.parseMessage(response), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                                    Toast.makeText(BusinessDetailActivity.this, ApiErrorParser.parseThrowable(t),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
