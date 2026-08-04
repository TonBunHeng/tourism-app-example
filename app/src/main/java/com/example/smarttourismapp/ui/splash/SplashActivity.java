package com.example.smarttourismapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.splashscreen.SplashScreen;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.MainActivity;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.ui.auth.LoginActivity;
import com.example.smarttourismapp.ui.onboarding.IntroductionActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() ->{
            Intent intent = new Intent(
                SplashActivity.this,
                IntroductionActivity.class
            );
            startActivity(intent);
            finish();
        },2000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}