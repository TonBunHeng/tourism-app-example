package com.example.smarttourismapp.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ActivityRegisterBinding;
import com.example.smarttourismapp.model.RegisterRequest;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.business.BusinessMainActivity;
import com.example.smarttourismapp.ui.traveller.TravellerMainActivity;

/**
 * Registration Screen matching the tourism-travel web app.
 * Supports Tourist and Business Owner registration, client-side validation,
 * server error mapping per input field, province selector, and auto navigation.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    private String selectedRole = "user"; // "user" or "business_owner"

    private final String[] provinces = {
            "Siem Reap",
            "Phnom Penh",
            "Battambang",
            "Kampot",
            "Kep",
            "Preah Sihanouk",
            "Koh Kong",
            "Mondulkiri",
            "Ratanakiri",
            "Pursat",
            "Kampong Cham",
            "Kampong Chhnang",
            "Kampong Speu",
            "Kampong Thom",
            "Kandal",
            "Kratie",
            "Preah Vihear",
            "Prey Veng",
            "Stung Treng",
            "Svay Rieng",
            "Takeo",
            "Oddar Meanchey",
            "Pailin",
            "Tboung Khmum"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupProvinceDropdown();
        setupRoleToggle();
        setupListeners();
        observeViewModel();
    }

    private void setupProvinceDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                provinces
        );
        binding.actvLocation.setAdapter(adapter);
    }

    private void setupRoleToggle() {
        binding.cardRoleTourist.setOnClickListener(v -> selectRole("user"));
        binding.cardRoleBusiness.setOnClickListener(v -> selectRole("business_owner"));
    }

    private void selectRole(String role) {
        this.selectedRole = role;
        int primaryColor = getColor(R.color.primary);
        int muteColor = getColor(R.color.mute);
        int textPrimary = getColor(R.color.text_primary);
        int bgActive = Color.parseColor("#EBF2FA");
        int bgDefault = getColor(R.color.background);

        if ("user".equals(role)) {
            binding.cardRoleTourist.setStrokeColor(primaryColor);
            binding.cardRoleTourist.setStrokeWidth(dpToPx(2));
            binding.cardRoleTourist.setCardBackgroundColor(bgActive);
            binding.tvRoleTouristTitle.setTextColor(primaryColor);

            binding.cardRoleBusiness.setStrokeColor(muteColor);
            binding.cardRoleBusiness.setStrokeWidth(dpToPx(1));
            binding.cardRoleBusiness.setCardBackgroundColor(bgDefault);
            binding.tvRoleBusinessTitle.setTextColor(textPrimary);

            binding.bioLayout.setHint(getString(R.string.auth_bio_tourist));
        } else {
            binding.cardRoleBusiness.setStrokeColor(primaryColor);
            binding.cardRoleBusiness.setStrokeWidth(dpToPx(2));
            binding.cardRoleBusiness.setCardBackgroundColor(bgActive);
            binding.tvRoleBusinessTitle.setTextColor(primaryColor);

            binding.cardRoleTourist.setStrokeColor(muteColor);
            binding.cardRoleTourist.setStrokeWidth(dpToPx(1));
            binding.cardRoleTourist.setCardBackgroundColor(bgDefault);
            binding.tvRoleTouristTitle.setTextColor(textPrimary);

            binding.bioLayout.setHint(getString(R.string.auth_bio_business));
        }
    }

    private void setupListeners() {
        binding.btnSignInLink.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void observeViewModel() {
        authViewModel.getRegisterState().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    User user = resource.getData();
                    if (user != null) {
                        onRegisterSuccess(user);
                    }
                    break;
                case ERROR:
                    setLoading(false);
                    handleRegisterError(resource.getMessage(), resource.getFieldErrors());
                    break;
            }
        });
    }

    private void handleRegister() {
        clearErrors();

        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String location = binding.actvLocation.getText() != null ? binding.actvLocation.getText().toString().trim() : "";
        String bio = binding.etBio.getText() != null ? binding.etBio.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(name)) {
            binding.nameLayout.setError("Full name is required.");
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.setError("Email address is required.");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Enter a valid email address.");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError("Password is required.");
            hasError = true;
        } else if (password.length() < 8) {
            binding.passwordLayout.setError("Password must be at least 8 characters.");
            hasError = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Please confirm your password.");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Passwords do not match.");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setPasswordConfirmation(confirmPassword);
        request.setRole(selectedRole);
        request.setPhone(!TextUtils.isEmpty(phone) ? phone : null);
        request.setLocation(!TextUtils.isEmpty(location) ? location : null);
        request.setBio(!TextUtils.isEmpty(bio) ? bio : null);

        authViewModel.register(request);
    }

    private void onRegisterSuccess(User user) {
        Toast.makeText(this, "Registration successful! Welcome to Angkor Tourism.", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (user.isBusinessOwner()) {
            intent = new Intent(RegisterActivity.this, BusinessMainActivity.class);
        } else {
            intent = new Intent(RegisterActivity.this, TravellerMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleRegisterError(String message, java.util.Map<String, String> fieldErrors) {
        if (fieldErrors != null) {
            if (fieldErrors.containsKey("name")) binding.nameLayout.setError(fieldErrors.get("name"));
            if (fieldErrors.containsKey("email")) binding.emailLayout.setError(fieldErrors.get("email"));
            if (fieldErrors.containsKey("password")) binding.passwordLayout.setError(fieldErrors.get("password"));
            if (fieldErrors.containsKey("phone")) binding.phoneLayout.setError(fieldErrors.get("phone"));
            if (fieldErrors.containsKey("location")) binding.locationLayout.setError(fieldErrors.get("location"));
        }

        if (!TextUtils.isEmpty(message)) {
            binding.tvErrorMessage.setText(message);
            binding.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void clearErrors() {
        binding.nameLayout.setError(null);
        binding.emailLayout.setError(null);
        binding.phoneLayout.setError(null);
        binding.locationLayout.setError(null);
        binding.bioLayout.setError(null);
        binding.passwordLayout.setError(null);
        binding.confirmPasswordLayout.setError(null);
        binding.tvErrorMessage.setVisibility(View.GONE);
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnRegister.setEnabled(false);
            binding.btnRegister.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnRegister.setEnabled(true);
            binding.btnRegister.setText(R.string.auth_btn_register);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
