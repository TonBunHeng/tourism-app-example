package com.example.tourism_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.tourism_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default to HomeFragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            binding.fabAiChat.show();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                binding.fabAiChat.show();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
                binding.fabAiChat.hide();
            } else if (itemId == R.id.nav_trips) {
                selectedFragment = new TripsFragment();
                binding.fabAiChat.hide();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                binding.fabAiChat.hide();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        binding.fabAiChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AiChatActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}