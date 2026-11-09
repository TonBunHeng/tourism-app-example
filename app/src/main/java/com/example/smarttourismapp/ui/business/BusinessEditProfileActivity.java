package com.example.smarttourismapp.ui.business;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.data.repository.TravelRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.Province;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessEditProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etName, etAddress, etPhone, etEmail, etWebsite, etDescription;
    private AutoCompleteTextView actvCategory, actvProvince;
    private ProgressBar pbLoading;
    private MaterialButton btnSave;

    private BusinessRepository businessRepository;
    private TravelRepository travelRepository;
    private AppPreference appPreference;
    private Business currentBusiness;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Province> provinceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        businessRepository = new BusinessRepository(this);
        travelRepository = new TravelRepository(this);
        appPreference = new AppPreference(this);
        currentBusiness = appPreference.getSelectedBusiness();

        initViews();
        loadDropdownData();
        populateFields();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarEditProfile);
        etName = findViewById(R.id.etEditBizName);
        actvCategory = findViewById(R.id.actvEditBizCategory);
        actvProvince = findViewById(R.id.actvEditBizProvince);
        etAddress = findViewById(R.id.etEditBizAddress);
        etPhone = findViewById(R.id.etEditBizPhone);
        etEmail = findViewById(R.id.etEditBizEmail);
        etWebsite = findViewById(R.id.etEditBizWebsite);
        etDescription = findViewById(R.id.etEditBizDescription);
        pbLoading = findViewById(R.id.pbLoadingEditProfile);
        btnSave = findViewById(R.id.btnSaveBusinessProfile);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void populateFields() {
        if (currentBusiness != null) {
            etName.setText(currentBusiness.getName());
            actvCategory.setText(currentBusiness.getCategory(), false);
            actvProvince.setText(currentBusiness.getProvince(), false);
            etAddress.setText(currentBusiness.getAddress());
            etPhone.setText(currentBusiness.getPhone());
            etEmail.setText(currentBusiness.getEmail());
            etWebsite.setText(currentBusiness.getWebsite());
            etDescription.setText(currentBusiness.getDescription());
        }
    }

    private void loadDropdownData() {
        travelRepository.getCategories(new TravelRepository.RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categoryList.clear();
                if (data != null) categoryList.addAll(data);
                List<String> names = new ArrayList<>();
                for (Category c : categoryList) names.add(c.getName());
                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(BusinessEditProfileActivity.this,
                        android.R.layout.simple_dropdown_item_1line, names);
                actvCategory.setAdapter(catAdapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });

        travelRepository.getProvinces(new TravelRepository.RepositoryCallback<List<Province>>() {
            @Override
            public void onSuccess(List<Province> data) {
                provinceList.clear();
                if (data != null) provinceList.addAll(data);
                List<String> names = new ArrayList<>();
                for (Province p : provinceList) names.add(p.getName());
                ArrayAdapter<String> provAdapter = new ArrayAdapter<>(BusinessEditProfileActivity.this,
                        android.R.layout.simple_dropdown_item_1line, names);
                actvProvince.setAdapter(provAdapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        if (name.isEmpty()) {
            Toast.makeText(this, "Business name is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentBusiness == null || currentBusiness.getId() == null) {
            Toast.makeText(this, "No active business found to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        pbLoading.setVisibility(View.VISIBLE);

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("address", etAddress.getText() != null ? etAddress.getText().toString().trim() : "");
        body.put("phone", etPhone.getText() != null ? etPhone.getText().toString().trim() : "");
        body.put("email", etEmail.getText() != null ? etEmail.getText().toString().trim() : "");
        body.put("website", etWebsite.getText() != null ? etWebsite.getText().toString().trim() : "");
        body.put("description", etDescription.getText() != null ? etDescription.getText().toString().trim() : "");

        String catName = actvCategory.getText().toString().trim();
        for (Category c : categoryList) {
            if (c.getName().equalsIgnoreCase(catName)) {
                body.put("category_id", c.getId());
                break;
            }
        }

        String provName = actvProvince.getText().toString().trim();
        for (Province p : provinceList) {
            if (p.getName().equalsIgnoreCase(provName)) {
                body.put("province_id", p.getId());
                break;
            }
        }

        businessRepository.updateBusiness(currentBusiness.getId(), body, new BusinessRepository.RepositoryCallback<Business>() {
            @Override
            public void onSuccess(Business updated) {
                btnSave.setEnabled(true);
                pbLoading.setVisibility(View.GONE);
                appPreference.saveSelectedBusiness(updated);
                Toast.makeText(BusinessEditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                btnSave.setEnabled(true);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(BusinessEditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

