package com.example.tourism_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tourism_app.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), navInsets.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvGoLogin.setOnClickListener(v -> finish());

        binding.btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String email = binding.etRegEmail.getText() != null ? binding.etRegEmail.getText().toString().trim() : "";
        String password = binding.etRegPassword.getText() != null ? binding.etRegPassword.getText().toString().trim() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString().trim() : "";

        boolean isValid = true;

        if (name.isEmpty()) {
            binding.tilName.setError("Please enter your full name");
            isValid = false;
        } else {
            binding.tilName.setError(null);
        }

        if (email.isEmpty()) {
            binding.tilRegEmail.setError("Please enter your email address");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilRegEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            binding.tilRegEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilRegPassword.setError("Please enter your password");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilRegPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            binding.tilRegPassword.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        if (isValid) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}