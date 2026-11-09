package com.example.smarttourismapp.ui.traveller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.ai.AIChatActivity;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.ui.booking.BookingsFragment;
import com.example.smarttourismapp.ui.favorite.FavoritesFragment;
import com.example.smarttourismapp.ui.notification.NotificationsActivity;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class TravellerMainActivity extends AppCompatActivity {

    private View btnHeaderUserPill;
    private TextView tvHeaderUserName, tvHeaderUserSubtitle, tvNotificationBadge;
    private FrameLayout btnHeaderNotifications;
    private ImageButton btnHeaderLogout;
    private BottomNavigationView bottomNav;
    private ExtendedFloatingActionButton fabAiAssistant;

    private HomeFragment homeFragment;
    private ExploreFragment exploreFragment;
    private BookingsFragment bookingsFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;

    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private AppPreference appPreference;
    private AuthRepository authRepository;

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

        appPreference = new AppPreference(this);
        authRepository = new AuthRepository(this);

        initViews();
        setupFragments(savedInstanceState);
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeaderInfo();
    }

    private void initViews() {
        btnHeaderUserPill = findViewById(R.id.btnHeaderUserPill);
        tvHeaderUserName = findViewById(R.id.tvHeaderUserName);
        tvHeaderUserSubtitle = findViewById(R.id.tvHeaderUserSubtitle);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        btnHeaderNotifications = findViewById(R.id.btnHeaderNotifications);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        bottomNav = findViewById(R.id.bottomNav);
        fabAiAssistant = findViewById(R.id.fabAiAssistant);

        if (btnHeaderUserPill != null) {
            btnHeaderUserPill.setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.nav_profile));
        }

        if (btnHeaderNotifications != null) {
            btnHeaderNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(TravellerMainActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        if (btnHeaderLogout != null) {
            btnHeaderLogout.setOnClickListener(v -> confirmSignOut());
        }

        fabAiAssistant.setOnClickListener(v -> {
            Intent intent = new Intent(TravellerMainActivity.this, AIChatActivity.class);
            startActivity(intent);
        });

        updateHeaderInfo();
    }

    private void updateHeaderInfo() {
        if (tvHeaderUserName == null || tvHeaderUserSubtitle == null) return;
        User user = appPreference.getUser();
        if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
            tvHeaderUserName.setText(user.getName());
            String loc = (user.getLocation() != null && !user.getLocation().trim().isEmpty())
                    ? user.getLocation()
                    : "Cambodia Explorer";
            tvHeaderUserSubtitle.setText(loc + " • Tap for profile");
        } else {
            tvHeaderUserName.setText("Traveler Hub");
            tvHeaderUserSubtitle.setText("Explore Cambodia • Tap for profile");
        }
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from Angkor Smart Tourism?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authRepository.logout(new AuthRepository.AuthCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(TravellerMainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TravellerMainActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Intent intent = new Intent(TravellerMainActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
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
