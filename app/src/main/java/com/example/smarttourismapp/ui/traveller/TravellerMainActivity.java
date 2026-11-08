package com.example.smarttourismapp.ui.traveller;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.ui.ai.AIChatActivity;
import com.example.smarttourismapp.ui.booking.BookingsFragment;
import com.example.smarttourismapp.ui.favorite.FavoritesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class TravellerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private ExtendedFloatingActionButton fabAiAssistant;

    private HomeFragment homeFragment;
    private ExploreFragment exploreFragment;
    private BookingsFragment bookingsFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;

    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_traveller_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        setupFragments(savedInstanceState);
        setupNavigation();
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        fabAiAssistant = findViewById(R.id.fabAiAssistant);

        fabAiAssistant.setOnClickListener(v -> {
            Intent intent = new Intent(TravellerMainActivity.this, AIChatActivity.class);
            startActivity(intent);
        });
    }

    private void setupFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            exploreFragment = new ExploreFragment();
            bookingsFragment = new BookingsFragment();
            favoritesFragment = new FavoritesFragment();
            profileFragment = new ProfileFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, homeFragment, "HOME")
                    .commit();
            activeFragment = homeFragment;
        } else {
            homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("HOME");
            exploreFragment = (ExploreFragment) fragmentManager.findFragmentByTag("EXPLORE");
            bookingsFragment = (BookingsFragment) fragmentManager.findFragmentByTag("BOOKINGS");
            favoritesFragment = (FavoritesFragment) fragmentManager.findFragmentByTag("FAVORITES");
            profileFragment = (ProfileFragment) fragmentManager.findFragmentByTag("PROFILE");

            if (homeFragment == null) homeFragment = new HomeFragment();
            if (exploreFragment == null) exploreFragment = new ExploreFragment();
            if (bookingsFragment == null) bookingsFragment = new BookingsFragment();
            if (favoritesFragment == null) favoritesFragment = new FavoritesFragment();
            if (profileFragment == null) profileFragment = new ProfileFragment();
        }
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment, "HOME");
                fabAiAssistant.show();
                return true;
            } else if (itemId == R.id.nav_explore) {
                switchFragment(exploreFragment, "EXPLORE");
                fabAiAssistant.show();
                return true;
            } else if (itemId == R.id.nav_bookings) {
                switchFragment(bookingsFragment, "BOOKINGS");
                fabAiAssistant.hide();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                switchFragment(favoritesFragment, "FAVORITES");
                fabAiAssistant.hide();
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment, "PROFILE");
                fabAiAssistant.hide();
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment targetFragment, String tag) {
        if (targetFragment == null || targetFragment == activeFragment) {
            return;
        }

        if (!targetFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .add(R.id.fragment_container, targetFragment, tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(targetFragment)
                    .commit();
        }
        activeFragment = targetFragment;
    }
}
