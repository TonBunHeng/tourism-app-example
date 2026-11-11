package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.ui.business.adapter.BusinessSelectorAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BusinessMainActivity extends AppCompatActivity {

    private View btnHeaderBusinessSelector;
    private TextView tvHeaderBusinessName, tvHeaderBusinessStatus, tvNotificationBadge;
    private FrameLayout btnHeaderNotifications;
    private ImageButton btnHeaderLogout;
    private BottomNavigationView bottomNav;

    private BusinessDashboardFragment dashboardFragment;
    private BusinessListingsFragment listingsFragment;
    private BusinessBookingsFragment bookingsFragment;
    private BusinessReviewsFragment reviewsFragment;
    private BusinessMoreFragment moreFragment;

    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private BusinessRepository businessRepository;
    private AuthRepository authRepository;
    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        businessRepository = new BusinessRepository(this);
        authRepository = new AuthRepository(this);
        appPreference = new AppPreference(this);

        initViews();
        resolveActiveBusiness();
        setupFragments(savedInstanceState);
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeaderInfo();
    }

    private void initViews() {
        btnHeaderBusinessSelector = findViewById(R.id.btnHeaderBusinessSelector);
        tvHeaderBusinessName = findViewById(R.id.tvHeaderBusinessName);
        tvHeaderBusinessStatus = findViewById(R.id.tvHeaderBusinessStatus);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        btnHeaderNotifications = findViewById(R.id.btnHeaderNotifications);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        bottomNav = findViewById(R.id.bottomNavBusiness);

        btnHeaderBusinessSelector.setOnClickListener(v -> showBusinessSwitchDialog());

        btnHeaderNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessMainActivity.this, BusinessNotificationsActivity.class);
            startActivity(intent);
        });

        btnHeaderLogout.setOnClickListener(v -> confirmSignOut());
    }

    private void resolveActiveBusiness() {
        Business current = appPreference.getSelectedBusiness();
        if (current == null) {
            User user = appPreference.getUser();
            if (user != null && user.getBusiness() != null) {
                appPreference.saveSelectedBusiness(user.getBusiness());
            } else {
                // Fetch from server
                businessRepository.getBusinesses(new BusinessRepository.RepositoryCallback<List<Business>>() {
                    @Override
                    public void onSuccess(List<Business> data) {
                        if (data != null && !data.isEmpty()) {
                            appPreference.saveSelectedBusiness(data.get(0));
                            updateHeaderInfo();
                            if (dashboardFragment != null) dashboardFragment.loadDashboard();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {}
                });
            }
        }
        updateHeaderInfo();
    }

    private void updateHeaderInfo() {
        Business biz = appPreference.getSelectedBusiness();
        if (biz != null) {
            tvHeaderBusinessName.setText(biz.getName());
            String status = biz.isVerified() ? "Verified • Tap to switch" : "Pending Verification";
            tvHeaderBusinessStatus.setText(status);
        } else {
            User user = appPreference.getUser();
            String name = (user != null) ? user.getName() + " Enterprise" : "Business Hub";
            tvHeaderBusinessName.setText(name);
            tvHeaderBusinessStatus.setText("Tap to add or switch business");
        }
    }

    private void setupFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            dashboardFragment = new BusinessDashboardFragment();
            listingsFragment = new BusinessListingsFragment();
            bookingsFragment = new BusinessBookingsFragment();
            reviewsFragment = new BusinessReviewsFragment();
            moreFragment = new BusinessMoreFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_business, dashboardFragment, "DASHBOARD")
                    .commit();
            activeFragment = dashboardFragment;
        } else {
            dashboardFragment = (BusinessDashboardFragment) fragmentManager.findFragmentByTag("DASHBOARD");
            listingsFragment = (BusinessListingsFragment) fragmentManager.findFragmentByTag("LISTINGS");
            bookingsFragment = (BusinessBookingsFragment) fragmentManager.findFragmentByTag("BOOKINGS");
            reviewsFragment = (BusinessReviewsFragment) fragmentManager.findFragmentByTag("REVIEWS");
            moreFragment = (BusinessMoreFragment) fragmentManager.findFragmentByTag("MORE");

            if (dashboardFragment == null) dashboardFragment = new BusinessDashboardFragment();
            if (listingsFragment == null) listingsFragment = new BusinessListingsFragment();
            if (bookingsFragment == null) bookingsFragment = new BusinessBookingsFragment();
            if (reviewsFragment == null) reviewsFragment = new BusinessReviewsFragment();
            if (moreFragment == null) moreFragment = new BusinessMoreFragment();
        }
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_business_dashboard) {
                switchFragment(dashboardFragment, "DASHBOARD");
                return true;
            } else if (itemId == R.id.nav_business_listings) {
                switchFragment(listingsFragment, "LISTINGS");
                return true;
            } else if (itemId == R.id.nav_business_bookings) {
                switchFragment(bookingsFragment, "BOOKINGS");
                return true;
            } else if (itemId == R.id.nav_business_reviews) {
                switchFragment(reviewsFragment, "REVIEWS");
                return true;
            } else if (itemId == R.id.nav_business_more) {
                switchFragment(moreFragment, "MORE");
                return true;
            }
            return false;
        });
    }

    public void navigateToTab(int tabId) {
        bottomNav.setSelectedItemId(tabId);
    }

    private void switchFragment(Fragment targetFragment, String tag) {
        if (targetFragment == null || targetFragment == activeFragment) {
            return;
        }

        if (!targetFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .add(R.id.fragment_container_business, targetFragment, tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(targetFragment)
                    .commit();
        }
        activeFragment = targetFragment;
    }

    public void showBusinessSwitchDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_switch_business, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        RecyclerView rv = dialogView.findViewById(R.id.rvDialogBusinesses);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAddNewBusinessInDialog);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseSwitchDialog);

        Long currentId = appPreference.getSelectedBusinessId();
        BusinessSelectorAdapter adapter = new BusinessSelectorAdapter(this, currentId, selectedBiz -> {
            appPreference.saveSelectedBusiness(selectedBiz);
            updateHeaderInfo();
            Toast.makeText(BusinessMainActivity.this, "Active: " + selectedBiz.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // Refresh fragments
            if (dashboardFragment != null) dashboardFragment.loadDashboard();
            if (bookingsFragment != null) bookingsFragment.loadBookings();
            if (reviewsFragment != null) reviewsFragment.loadReviews();
            if (listingsFragment != null) listingsFragment.refreshListings();
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        businessRepository.getBusinesses(new BusinessRepository.RepositoryCallback<List<Business>>() {
            @Override
            public void onSuccess(List<Business> data) {
                adapter.setBusinesses(data, currentId);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BusinessMainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(BusinessMainActivity.this, ManageBusinessesActivity.class));
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from Angkor Business Hub?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authRepository.logout(new AuthRepository.AuthCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(BusinessMainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BusinessMainActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Intent intent = new Intent(BusinessMainActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
