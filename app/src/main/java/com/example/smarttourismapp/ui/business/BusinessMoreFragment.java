package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.chip.Chip;

public class BusinessMoreFragment extends Fragment {

    private TextView tvMoreBusinessInitial, tvMoreBusinessName, tvMoreOwnerName, tvMoreBusinessCategory;
    private Chip chipMoreStatus;

    private View layoutMenuHours, layoutMenuReports, layoutMenuProfile, layoutMenuManageBiz, layoutMenuNotifications, layoutMenuSettings, layoutMenuLogout;

    private AppPreference appPreference;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_more, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreference = new AppPreference(requireContext());
        authRepository = new AuthRepository(requireContext());
        displayBusinessInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayBusinessInfo();
    }

    private void initViews(View view) {
        tvMoreBusinessInitial = view.findViewById(R.id.tvMoreBusinessInitial);
        tvMoreBusinessName = view.findViewById(R.id.tvMoreBusinessName);
        tvMoreOwnerName = view.findViewById(R.id.tvMoreOwnerName);
        tvMoreBusinessCategory = view.findViewById(R.id.tvMoreBusinessCategory);
        chipMoreStatus = view.findViewById(R.id.chipMoreStatus);

        layoutMenuHours = view.findViewById(R.id.layoutMenuHours);
        layoutMenuReports = view.findViewById(R.id.layoutMenuReports);
        layoutMenuProfile = view.findViewById(R.id.layoutMenuProfile);
        layoutMenuManageBiz = view.findViewById(R.id.layoutMenuManageBiz);
        layoutMenuNotifications = view.findViewById(R.id.layoutMenuNotifications);
        layoutMenuSettings = view.findViewById(R.id.layoutMenuSettings);
        layoutMenuLogout = view.findViewById(R.id.layoutMenuLogout);
    }

    private void displayBusinessInfo() {
        Business biz = appPreference.getSelectedBusiness();
        User user = appPreference.getUser();

        if (biz != null) {
            String name = biz.getName();
            tvMoreBusinessName.setText(name);
            tvMoreBusinessInitial.setText(!name.isEmpty() ? name.substring(0, 1).toUpperCase() : "B");
            tvMoreBusinessCategory.setText(biz.getCategory() + " • " + biz.getProvince());

            if (biz.isVerified()) {
                chipMoreStatus.setText("Verified");
                chipMoreStatus.setChipBackgroundColorResource(R.color.success);
            } else {
                chipMoreStatus.setText("Pending");
                chipMoreStatus.setChipBackgroundColorResource(R.color.warning);
            }
        } else if (user != null) {
            tvMoreBusinessName.setText(user.getName() + " Enterprises");
            tvMoreBusinessInitial.setText(!user.getName().isEmpty() ? user.getName().substring(0, 1).toUpperCase() : "U");
            tvMoreBusinessCategory.setText("Travel Services • Cambodia");
        }

        if (user != null) {
            tvMoreOwnerName.setText("Owner: " + user.getName());
        }
    }

    private void setupListeners() {
        layoutMenuHours.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BusinessHoursActivity.class));
        });

        layoutMenuReports.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BusinessReportsActivity.class));
        });

        layoutMenuProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BusinessEditProfileActivity.class));
        });

        layoutMenuManageBiz.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ManageBusinessesActivity.class));
        });

        layoutMenuNotifications.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BusinessNotificationsActivity.class));
        });

        layoutMenuSettings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BusinessSettingsActivity.class));
        });

        layoutMenuLogout.setOnClickListener(v -> confirmSignOut());
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from the Business Owner Hub?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authRepository.logout(new AuthRepository.AuthCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(requireContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(requireContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            if (getActivity() != null) getActivity().finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Intent intent = new Intent(requireContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            if (getActivity() != null) getActivity().finish();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

