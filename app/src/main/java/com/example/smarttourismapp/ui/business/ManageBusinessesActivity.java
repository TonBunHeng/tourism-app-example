package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.ui.business.adapter.BusinessSelectorAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageBusinessesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvBusinesses;
    private ProgressBar pbLoading;
    private MaterialButton btnRegisterNew;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private BusinessSelectorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_businesses);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new BusinessRepository(this);
        appPreference = new AppPreference(this);

        initViews();
        setupRecyclerView();
        loadBusinesses();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarManageBusinesses);
        rvBusinesses = findViewById(R.id.rvManageBusinessesList);
        pbLoading = findViewById(R.id.pbLoadingManageBusinesses);
        btnRegisterNew = findViewById(R.id.btnRegisterNewBusiness);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnRegisterNew.setOnClickListener(v -> showRegisterBusinessDialog());
    }

    private void setupRecyclerView() {
        Long currentId = appPreference.getSelectedBusinessId();
        adapter = new BusinessSelectorAdapter(this, currentId, selectedBiz -> {
            appPreference.saveSelectedBusiness(selectedBiz);
            Toast.makeText(ManageBusinessesActivity.this, "Active Business: " + selectedBiz.getName(), Toast.LENGTH_SHORT).show();
            finish();
        });
        rvBusinesses.setLayoutManager(new LinearLayoutManager(this));
        rvBusinesses.setAdapter(adapter);
    }

    private void loadBusinesses() {
        pbLoading.setVisibility(View.VISIBLE);
        repository.getBusinesses(new BusinessRepository.RepositoryCallback<List<Business>>() {
            @Override
            public void onSuccess(List<Business> data) {
                pbLoading.setVisibility(View.GONE);
                Long currentId = appPreference.getSelectedBusinessId();
                adapter.setBusinesses(data, currentId);
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(ManageBusinessesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegisterBusinessDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_service, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Register New Business")
                .setMessage("Submit your new business profile for verification.")
                .setView(dialogView)
                .create();

        EditText etName = dialogView.findViewById(R.id.etServiceName);
        EditText etDesc = dialogView.findViewById(R.id.etServiceDesc);
        EditText etPrice = dialogView.findViewById(R.id.etServicePrice);
        EditText etDuration = dialogView.findViewById(R.id.etServiceDuration);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelService);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveService);

        etName.setHint("Business Enterprise Name *");
        etDesc.setHint("Description & Tourism Services");
        etPrice.setVisibility(View.GONE);
        etDuration.setVisibility(View.GONE);
        dialogView.findViewById(R.id.etServiceCapacity).setVisibility(View.GONE);
        dialogView.findViewById(R.id.switchServiceActive).setVisibility(View.GONE);

        btnSave.setText("Register Enterprise");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Business name is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("description", etDesc.getText().toString().trim());

            repository.createBusiness(body, new BusinessRepository.RepositoryCallback<Business>() {
                @Override
                public void onSuccess(Business created) {
                    Toast.makeText(ManageBusinessesActivity.this, "Business registered successfully! Pending admin approval.", Toast.LENGTH_LONG).show();
                    appPreference.saveSelectedBusiness(created);
                    dialog.dismiss();
                    loadBusinesses();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ManageBusinessesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}

