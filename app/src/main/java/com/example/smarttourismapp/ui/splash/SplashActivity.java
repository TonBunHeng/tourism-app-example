package com.example.smarttourismapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.ui.business.BusinessMainActivity;
import com.example.smarttourismapp.ui.onboarding.OnboardingActivity;
import com.example.smarttourismapp.ui.traveller.TravellerMainActivity;
import com.example.smarttourismapp.utils.AppPreference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(this::checkNavigation, 1500);
    }

    private void checkNavigation() {
        AppPreference preference = new AppPreference(this);

        if (preference.isFirstTime()) {
            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            finish();
            return;
        }

        if (preference.isLoggedIn()) {
            User user = preference.getUser();
            Intent intent;
            if (user != null && user.isBusinessOwner()) {
                intent = new Intent(SplashActivity.this, BusinessMainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, TravellerMainActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }

        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
        finish();
    }
}
