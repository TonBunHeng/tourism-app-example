package com.example.smarttourismapp.ui.business;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class BusinessSettingsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etCurrent, etNew, etConfirm;
    private MaterialButton btnSubmitPassword;
    private SwitchMaterial switchNotifyBookings, switchNotifyReviews, switchDarkMode;

    private AuthRepository authRepository;
    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository(this);
        appPreference = new AppPreference(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarSettings);
        etCurrent = findViewById(R.id.etCurrentPassword);
        etNew = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnSubmitPassword = findViewById(R.id.btnSubmitChangePassword);

        switchNotifyBookings = findViewById(R.id.switchNotifyBookings);
        switchNotifyReviews = findViewById(R.id.switchNotifyReviews);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        toolbar.setNavigationOnClickListener(v -> finish());
        switchDarkMode.setChecked(appPreference.isDarkMode());
    }

    private void setupListeners() {
        btnSubmitPassword.setOnClickListener(v -> {
            String current = etCurrent.getText() != null ? etCurrent.getText().toString().trim() : "";
            String newPass = etNew.getText() != null ? etNew.getText().toString().trim() : "";
            String confirm = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";

            if (current.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please enter all password fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmitPassword.setEnabled(false);
            authRepository.changePassword(current, newPass, confirm, new AuthRepository.AuthCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    btnSubmitPassword.setEnabled(true);
                    Toast.makeText(BusinessSettingsActivity.this, "Password updated successfully!", Toast.LENGTH_LONG).show();
                    etCurrent.setText("");
                    etNew.setText("");
                    etConfirm.setText("");
                }

                @Override
                public void onError(String errorMessage) {
                    btnSubmitPassword.setEnabled(true);
                    Toast.makeText(BusinessSettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPreference.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }
}

