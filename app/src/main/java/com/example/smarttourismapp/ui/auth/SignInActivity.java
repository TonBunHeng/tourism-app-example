package com.example.smarttourismapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ActivitySignInBinding;
import com.example.smarttourismapp.model.GoogleLoginRequest;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.business.BusinessMainActivity;
import com.example.smarttourismapp.ui.traveller.TravellerMainActivity;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Sign In Screen matching the tourism-travel web app.
 * Supports email/password authentication, field validation, server error binding,
 * Google OAuth sign-in, Remember Me persistence, and password reset instructions.
 */
public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private AuthViewModel authViewModel;
    private AppPreference appPreference;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        appPreference = new AppPreference(this);

        initGoogleSignIn();
        restoreSavedCredentials();
        setupListeners();
        observeViewModel();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    } else {
                        setLoading(false);
                    }
                }
        );
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String token = account.getIdToken() != null ? account.getIdToken() : account.getId();
                String email = account.getEmail();
                String name = account.getDisplayName();
                String avatar = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;

                GoogleLoginRequest request = new GoogleLoginRequest(token, email, name, avatar);
                if (account.getId() != null) {
                    request.setGoogleId(account.getId());
                }
                authViewModel.googleLogin(request);
            }
        } catch (ApiException e) {
            setLoading(false);
            binding.tvErrorMessage.setText("Google Sign-In failed (" + e.getStatusCode() + "). Please try again.");
            binding.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void restoreSavedCredentials() {
        if (appPreference.isRememberMe()) {
            binding.cbRemember.setChecked(true);
            String savedEmail = appPreference.getSavedEmail();
            if (!TextUtils.isEmpty(savedEmail)) {
                binding.etEmail.setText(savedEmail);
                binding.etPassword.requestFocus();
            }
        }
    }

    private void setupListeners() {
        binding.btnSignIn.setOnClickListener(v -> handleSignIn());

        binding.btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
        });

        binding.btnForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        binding.btnGoogle.setOnClickListener(v -> {
            clearErrors();
            setLoading(true);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void observeViewModel() {
        authViewModel.getLoginState().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    User user = resource.getData();
                    if (user != null) {
                        onLoginSuccess(user);
                    }
                    break;
                case ERROR:
                    setLoading(false);
                    handleLoginError(resource.getMessage(), resource.getFieldErrors());
                    break;
            }
        });
    }

    private void handleSignIn() {
        clearErrors();

        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.setError(getString(R.string.common_text_input_email_hint));
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Please enter a valid email address.");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError(getString(R.string.common_text_input_password_hint));
            hasError = true;
        }

        if (hasError) {
            return;
        }

        boolean remember = binding.cbRemember.isChecked();
        authViewModel.login(email, password, remember);
    }

    private void onLoginSuccess(User user) {
        Toast.makeText(this, "Welcome, " + user.getName() + "!", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (user.isBusinessOwner()) {
            intent = new Intent(SignInActivity.this, BusinessMainActivity.class);
        } else {
            intent = new Intent(SignInActivity.this, TravellerMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleLoginError(String message, java.util.Map<String, String> fieldErrors) {
        if (fieldErrors != null) {
            if (fieldErrors.containsKey("email")) {
                binding.emailLayout.setError(fieldErrors.get("email"));
            }
            if (fieldErrors.containsKey("password")) {
                binding.passwordLayout.setError(fieldErrors.get("password"));
            }
        }

        if (!TextUtils.isEmpty(message)) {
            binding.tvErrorMessage.setText(message);
            binding.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showForgotPasswordDialog() {
        String currentEmail = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("To reset your password, an email link can be sent to your account" +
                        (!TextUtils.isEmpty(currentEmail) ? " (" + currentEmail + ")" : "") +
                        ".\n\nYou can also contact the Angkor Tourism Helpdesk for immediate account recovery.")
                .setPositiveButton("Close", null)
                .show();
    }

    private void clearErrors() {
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);
        binding.tvErrorMessage.setVisibility(View.GONE);
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnSignIn.setEnabled(false);
            binding.btnSignIn.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnSignIn.setEnabled(true);
            binding.btnSignIn.setText(R.string.common_btn_sign_in);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
