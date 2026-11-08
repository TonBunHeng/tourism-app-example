package com.example.smarttourismapp.ui.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.databinding.ActivitySupportBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.utils.AppPreference;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    private ActivitySupportBinding binding;
    private ApiService apiService;
    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(this);
        appPreference = new AppPreference(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupEmergencyHotlines();
        setupContactForm();
    }

    private void setupEmergencyHotlines() {
        binding.btnDialPolice.setOnClickListener(v -> dialNumber("117"));
        binding.btnDialFire.setOnClickListener(v -> dialNumber("118"));
        binding.btnDialAmbulance.setOnClickListener(v -> dialNumber("119"));
        binding.btnDialTouristPolice.setOnClickListener(v -> dialNumber("+85523724793"));
    }

    private void dialNumber(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open dialer for " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupContactForm() {
        binding.btnSendContact.setOnClickListener(v -> {
            String subject = binding.etContactSubject.getText() != null ?
                    binding.etContactSubject.getText().toString().trim() : "";
            String message = binding.etContactMessage.getText() != null ?
                    binding.etContactMessage.getText().toString().trim() : "";

            if (TextUtils.isEmpty(subject)) {
                binding.etContactSubject.setError("Subject is required");
                return;
            }
            if (TextUtils.isEmpty(message)) {
                binding.etContactMessage.setError("Message cannot be empty");
                return;
            }

            binding.btnSendContact.setEnabled(false);
            binding.btnSendContact.setText("Sending...");

            Map<String, String> body = new HashMap<>();
            body.put("subject", subject);
            body.put("message", message);
            if (appPreference.getUser() != null) {
                body.put("name", appPreference.getUser().getName());
                body.put("email", appPreference.getUser().getEmail());
            }

            apiService.sendContact(body).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                    binding.btnSendContact.setEnabled(true);
                    binding.btnSendContact.setText("Send Message");
                    Toast.makeText(SupportActivity.this, "Your message has been sent to our tourism support team!", Toast.LENGTH_LONG).show();
                    binding.etContactSubject.setText("");
                    binding.etContactMessage.setText("");
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                    binding.btnSendContact.setEnabled(true);
                    binding.btnSendContact.setText("Send Message");
                    Toast.makeText(SupportActivity.this, "Thank you! Your feedback has been recorded.", Toast.LENGTH_SHORT).show();
                    binding.etContactSubject.setText("");
                    binding.etContactMessage.setText("");
                }
            });
        });
    }
}
