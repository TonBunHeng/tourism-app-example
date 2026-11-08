package com.example.smarttourismapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private DotsIndicator dotIndicator;
    private Button btnNext, btnSkip;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Bind to UI by id
        viewPager = findViewById(R.id.viewPager);
        dotIndicator = findViewById(R.id.dotIndicator);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Add data into items list (image, title, description)
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.onboarding_1, getString(R.string.onboarding_title_1), getString(R.string.onboarding_description1)));
        items.add(new OnboardingItem(R.drawable.onboarding_2, getString(R.string.onboarding_title_2), getString(R.string.onboarding_description2)));
        items.add(new OnboardingItem(R.drawable.onboarding_3, getString(R.string.onboarding_title_3), getString(R.string.onboarding_description3)));
        items.add(new OnboardingItem(R.drawable.onboarding_4, getString(R.string.onboarding_title_4), getString(R.string.onboarding_description4)));

        // Add items data into the adapter Object
        adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter); // get data from adapter Object to display on UI

        // Dynamic indicator follow by total of viewPager
        dotIndicator.attachTo(viewPager);

        // check viewPager is to the last viewPager? show only btnNext
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == items.size() - 1 ){
                    btnNext.setText(R.string.common_btn_get_start);
                    btnSkip.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setText(R.string.common_btn_next);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        // Click on btnNext and to new viewPager
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }else {
                navigateToSignIn();
            }
        });

        // Click on btnSkip to SignInActivity directly
        btnSkip.setOnClickListener(v -> navigateToSignIn());

    }
    // Redirect to Sign in screen
    private void navigateToSignIn(){
        new com.example.smarttourismapp.utils.AppPreference(this).setKeyFirstTimeLaunch(false);
        startActivity(new Intent(OnboardingActivity.this, SignInActivity.class));
        finish();
    }
}