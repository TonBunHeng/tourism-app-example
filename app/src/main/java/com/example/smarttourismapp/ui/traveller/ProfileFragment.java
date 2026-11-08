package com.example.smarttourismapp.ui.traveller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.ui.support.SupportActivity;
import com.example.smarttourismapp.utils.AppPreference;
import com.example.smarttourismapp.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileAvatar, btnUploadAvatar;
    private TextView tvProfileName, tvProfileEmail, tvProfileLocation, tvProfileBio;
    private Chip chipRole;
    private MaterialButton btnEditProfile, btnChangePassword, btnSignOut;
    private View rowLanguage, rowSupport;
    private TextView tvCurrentLanguage, btnDeleteAccount;
    private MaterialSwitch switchDarkMode;

    private AuthRepository authRepository;
    private AppPreference appPreference;
    private ApiService apiService;
    private User currentUser;

    private final ActivityResultLauncher<String> avatarPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleAvatarSelected
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository(requireContext());
        appPreference = new AppPreference(requireContext());
        apiService = ApiClient.getApiService(requireContext());

        initViews(view);
        setupUserViews();
        setupListeners();
        fetchFreshProfile();
    }

    private void initViews(View view) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        btnUploadAvatar = view.findViewById(R.id.btnUploadAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileLocation = view.findViewById(R.id.tvProfileLocation);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);
        chipRole = view.findViewById(R.id.chipRole);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        rowLanguage = view.findViewById(R.id.rowLanguage);
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        rowSupport = view.findViewById(R.id.rowSupport);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
    }

    private void setupUserViews() {
        currentUser = appPreference.getUser();
        displayUserData(currentUser);

        // Language display
        String currentLang = appPreference.getLanguage();
        if ("km".equalsIgnoreCase(currentLang)) {
            tvCurrentLanguage.setText("ខ្មែរ (Khmer)");
        } else {
            tvCurrentLanguage.setText("English");
        }

        // Dark mode display
        switchDarkMode.setChecked(appPreference.isDarkMode());
    }

    private void displayUserData(User user) {
        if (user == null) return;
        tvProfileName.setText(user.getName() != null ? user.getName() : "Traveler");
        tvProfileEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        chipRole.setText(user.isBusinessOwner() ? "Business Owner" : "Traveler / Tourist");

        if (!TextUtils.isEmpty(user.getLocation())) {
            tvProfileLocation.setText(user.getLocation());
            tvProfileLocation.setVisibility(View.VISIBLE);
        } else {
            tvProfileLocation.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(user.getBio())) {
            tvProfileBio.setText(user.getBio());
            tvProfileBio.setVisibility(View.VISIBLE);
        } else {
            tvProfileBio.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(user.getAvatar())) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivProfileAvatar);
        }
    }

    private void setupListeners() {
        btnUploadAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnSignOut.setOnClickListener(v -> performSignOut());

        rowLanguage.setOnClickListener(v -> showLanguageDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPreference.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        rowSupport.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SupportActivity.class);
            startActivity(intent);
        });

        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void fetchFreshProfile() {
        authRepository.getCurrentUser(new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (isAdded() && user != null) {
                    currentUser = user;
                    displayUserData(user);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Keep displaying cached user
            }
        });
    }

    private void handleAvatarSelected(Uri uri) {
        if (uri == null || !isAdded()) return;

        try {
            File cacheFile = new File(requireContext().getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(cacheFile)) {
                if (in == null) return;
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            Toast.makeText(requireContext(), "Uploading avatar...", Toast.LENGTH_SHORT).show();
            authRepository.uploadAvatar(cacheFile, new AuthRepository.AuthCallback<User>() {
                @Override
                public void onSuccess(User updatedUser) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
                    currentUser = updatedUser;
                    displayUserData(updatedUser);
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Avatar upload failed", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to read image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etEditName = dialogView.findViewById(R.id.etEditName);
        TextInputEditText etEditPhone = dialogView.findViewById(R.id.etEditPhone);
        TextInputEditText etEditLocation = dialogView.findViewById(R.id.etEditLocation);
        TextInputEditText etEditBio = dialogView.findViewById(R.id.etEditBio);

        if (currentUser != null) {
            etEditName.setText(currentUser.getName());
            etEditPhone.setText(currentUser.getPhone());
            etEditLocation.setText(currentUser.getLocation());
            etEditBio.setText(currentUser.getBio());
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etEditName.getText() != null ? etEditName.getText().toString().trim() : "";
                    String phone = etEditPhone.getText() != null ? etEditPhone.getText().toString().trim() : "";
                    String location = etEditLocation.getText() != null ? etEditLocation.getText().toString().trim() : "";
                    String bio = etEditBio.getText() != null ? etEditBio.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(requireContext(), "Saving profile...", Toast.LENGTH_SHORT).show();
                    authRepository.updateProfile(name, phone, location, bio, new AuthRepository.AuthCallback<User>() {
                        @Override
                        public void onSuccess(User updated) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                            currentUser = updated;
                            displayUserData(updated);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Could not update profile", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmNewPassword = dialogView.findViewById(R.id.etConfirmNewPassword);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String current = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
                    String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
                    String confirm = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(current)) {
                        Toast.makeText(requireContext(), "Enter your current password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 8) {
                        Toast.makeText(requireContext(), "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirm)) {
                        Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(requireContext(), "Updating password...", Toast.LENGTH_SHORT).show();
                    authRepository.changePassword(current, newPass, confirm, new AuthRepository.AuthCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "Failed to change password", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "ខ្មែរ (Khmer)"};
        int selectedIndex = "km".equalsIgnoreCase(appPreference.getLanguage()) ? 1 : 0;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select App Language / ជ្រើសរើសភាសា")
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String langCode = which == 1 ? "km" : "en";
                    LocaleHelper.setLocale(requireContext(), langCode);
                    dialog.dismiss();

                    // Recreate activity to apply locale strings across UI
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.recreate();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Request Account Erasure")
                .setMessage("Are you sure you want to request deletion of your account and personal travel history? This action is irreversible once processed.")
                .setPositiveButton("Submit Request", (dialog, which) -> {
                    Map<String, String> body = new HashMap<>();
                    body.put("reason", "User requested deletion from Android mobile app");

                    apiService.requestAccountDeletion(body).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                            Toast.makeText(requireContext(), "Erasure request submitted. Signing out...", Toast.LENGTH_LONG).show();
                            performSignOut();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                            performSignOut();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performSignOut() {
        authRepository.logout(new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                navigateToSignIn();
            }

            @Override
            public void onError(String errorMessage) {
                navigateToSignIn();
            }
        });
    }

    private void navigateToSignIn() {
        if (!isAdded()) return;
        Intent intent = new Intent(requireContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
