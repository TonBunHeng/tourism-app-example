package com.example.smarttourismapp.ui.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class BusinessMainActivity extends AppCompatActivity {

    private TextView tvOwnerGreeting, tvBusinessName, tvBusinessCategory, tvBusinessLocation, tvBusinessDescription;
    private TextView tvOwnerName, tvOwnerEmail, tvOwnerPhone;
    private Chip chipStatus;
    private ImageButton btnLogout;
    private MaterialButton btnSignOut;

    private AuthRepository authRepository;
    private AppPreference appPreference;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository(this);
        appPreference = new AppPreference(this);
        currentUser = appPreference.getUser();

        initViews();
        displayBusinessData();
        setupListeners();
    }

    private void initViews() {
        tvOwnerGreeting = findViewById(R.id.tvOwnerGreeting);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessCategory = findViewById(R.id.tvBusinessCategory);
        tvBusinessLocation = findViewById(R.id.tvBusinessLocation);
        tvBusinessDescription = findViewById(R.id.tvBusinessDescription);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvOwnerEmail = findViewById(R.id.tvOwnerEmail);
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone);
        chipStatus = findViewById(R.id.chipStatus);
        btnLogout = findViewById(R.id.btnLogout);
        btnSignOut = findViewById(R.id.btnSignOut);
    }

    private void displayBusinessData() {
        if (currentUser != null) {
            tvOwnerGreeting.setText("Welcome, " + currentUser.getName());
            tvOwnerName.setText("Owner: " + currentUser.getName());
            tvOwnerEmail.setText("Email: " + currentUser.getEmail());

            if (!TextUtils.isEmpty(currentUser.getPhone())) {
                tvOwnerPhone.setText("Phone: " + currentUser.getPhone());
            } else {
                tvOwnerPhone.setText("Phone: Not specified");
            }

            Business biz = currentUser.getBusiness();
            if (biz != null) {
                tvBusinessName.setText(biz.getName() != null ? biz.getName() : currentUser.getName() + " Enterprise");
                if (!TextUtils.isEmpty(biz.getCategory())) {
                    tvBusinessCategory.setText("Category: " + biz.getCategory());
                }
                if (!TextUtils.isEmpty(biz.getProvince())) {
                    tvBusinessLocation.setText("Location: " + biz.getProvince());
                }
                if (!TextUtils.isEmpty(biz.getStatus())) {
                    chipStatus.setText(biz.getStatus());
                }
            } else {
                tvBusinessName.setText(currentUser.getName() + " Travel Services");
                if (!TextUtils.isEmpty(currentUser.getLocation())) {
                    tvBusinessLocation.setText("Location: " + currentUser.getLocation());
                }
                if (!TextUtils.isEmpty(currentUser.getBio())) {
                    tvBusinessDescription.setText(currentUser.getBio());
                }
            }
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> signOut());
        btnSignOut.setOnClickListener(v -> signOut());
    }

    private void signOut() {
        authRepository.logout(new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(BusinessMainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BusinessMainActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Intent intent = new Intent(BusinessMainActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
