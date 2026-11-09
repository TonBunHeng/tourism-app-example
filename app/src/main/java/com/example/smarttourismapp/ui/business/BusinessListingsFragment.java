package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.BusinessImage;
import com.example.smarttourismapp.model.BusinessPromotion;
import com.example.smarttourismapp.model.BusinessService;
import com.example.smarttourismapp.ui.business.adapter.BusinessGalleryAdapter;
import com.example.smarttourismapp.ui.business.adapter.BusinessPromotionAdapter;
import com.example.smarttourismapp.ui.business.adapter.BusinessServiceAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessListingsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton fabAdd;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private Long businessId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_listings, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutListings);
        viewPager = view.findViewById(R.id.viewPagerListings);
        fabAdd = view.findViewById(R.id.fabAddListingItem);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new BusinessRepository(requireContext());
        appPreference = new AppPreference(requireContext());

        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;

        setupViewPager();
        setupFab();
    }

    private void setupViewPager() {
        ListingsPagerAdapter adapter = new ListingsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Services");
                    break;
                case 1:
                    tab.setText("Promotions");
                    break;
                case 2:
                    tab.setText("Gallery");
                    break;
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        fabAdd.setText("+ Add Service");
                        break;
                    case 1:
                        fabAdd.setText("+ Add Promo");
                        break;
                    case 2:
                        fabAdd.setText("+ Add Photo");
                        break;
                }
            }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem();
            if (currentTab == 0) {
                showManageServiceDialog(null);
            } else if (currentTab == 1) {
                showManagePromoDialog(null);
            } else if (currentTab == 2) {
                showAddImageDialog();
            }
        });
    }

    // ==========================================
    // Dialogs: Service, Promotion, Image
    // ==========================================
    public void showManageServiceDialog(BusinessService editingService) {
        if (businessId == null) {
            Toast.makeText(requireContext(), "Please select an active business first.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_manage_service, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogServiceTitle);
        EditText etName = dialogView.findViewById(R.id.etServiceName);
        EditText etDesc = dialogView.findViewById(R.id.etServiceDesc);
        EditText etPrice = dialogView.findViewById(R.id.etServicePrice);
        EditText etDuration = dialogView.findViewById(R.id.etServiceDuration);
        EditText etCapacity = dialogView.findViewById(R.id.etServiceCapacity);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchServiceActive);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelService);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveService);

        if (editingService != null) {
            tvTitle.setText("Edit Service / Package");
            etName.setText(editingService.getName());
            etDesc.setText(editingService.getDescription());
            etPrice.setText(String.valueOf(editingService.getPrice()));
            etDuration.setText(editingService.getDuration());
            etCapacity.setText(String.valueOf(editingService.getCapacity()));
            switchActive.setChecked(editingService.isActive());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Name and price are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = 0;
            try { price = Double.parseDouble(priceStr); } catch (Exception ignored) {}

            int capacity = 1;
            try { capacity = Integer.parseInt(etCapacity.getText().toString().trim()); } catch (Exception ignored) {}

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("description", etDesc.getText().toString().trim());
            body.put("price", price);
            body.put("duration", etDuration.getText().toString().trim());
            body.put("capacity", capacity);
            body.put("is_active", switchActive.isChecked());
            body.put("status", switchActive.isChecked() ? "active" : "inactive");

            if (editingService == null) {
                repository.createService(businessId, body, new BusinessRepository.RepositoryCallback<BusinessService>() {
                    @Override
                    public void onSuccess(BusinessService data) {
                        Toast.makeText(requireContext(), "Service created successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setupViewPager();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repository.updateService(businessId, editingService.getId(), body, new BusinessRepository.RepositoryCallback<BusinessService>() {
                    @Override
                    public void onSuccess(BusinessService data) {
                        Toast.makeText(requireContext(), "Service updated successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setupViewPager();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    public void showManagePromoDialog(BusinessPromotion editingPromo) {
        if (businessId == null) {
            Toast.makeText(requireContext(), "Please select an active business first.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_manage_promotion, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogPromoTitle);
        EditText etTitle = dialogView.findViewById(R.id.etPromoTitle);
        EditText etCode = dialogView.findViewById(R.id.etPromoCode);
        EditText etDiscount = dialogView.findViewById(R.id.etPromoDiscount);
        EditText etStart = dialogView.findViewById(R.id.etPromoStart);
        EditText etEnd = dialogView.findViewById(R.id.etPromoEnd);
        EditText etDesc = dialogView.findViewById(R.id.etPromoDesc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelPromo);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSavePromo);

        if (editingPromo != null) {
            tvTitle.setText("Edit Promotion");
            etTitle.setText(editingPromo.getTitle());
            etCode.setText(editingPromo.getCode());
            etDiscount.setText(String.valueOf(editingPromo.getDiscountPercent().intValue()));
            etStart.setText(editingPromo.getStartDate());
            etEnd.setText(editingPromo.getEndDate());
            etDesc.setText(editingPromo.getDescription());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            String discountStr = etDiscount.getText().toString().trim();

            if (title.isEmpty() || code.isEmpty() || discountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Title, code, and discount are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            double discount = 0;
            try { discount = Double.parseDouble(discountStr); } catch (Exception ignored) {}

            Map<String, Object> body = new HashMap<>();
            body.put("title", title);
            body.put("code", code);
            body.put("discount_percent", discount);
            body.put("start_date", etStart.getText().toString().trim());
            body.put("end_date", etEnd.getText().toString().trim());
            body.put("description", etDesc.getText().toString().trim());
            body.put("is_active", true);

            if (editingPromo == null) {
                repository.createPromotion(businessId, body, new BusinessRepository.RepositoryCallback<BusinessPromotion>() {
                    @Override
                    public void onSuccess(BusinessPromotion data) {
                        Toast.makeText(requireContext(), "Promotion created successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setupViewPager();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repository.updatePromotion(businessId, editingPromo.getId(), body, new BusinessRepository.RepositoryCallback<BusinessPromotion>() {
                    @Override
                    public void onSuccess(BusinessPromotion data) {
                        Toast.makeText(requireContext(), "Promotion updated successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setupViewPager();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    public void showAddImageDialog() {
        if (businessId == null) {
            Toast.makeText(requireContext(), "Please select an active business first.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_image, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etUrl = dialogView.findViewById(R.id.etImageUrl);
        EditText etCaption = dialogView.findViewById(R.id.etImageCaption);
        CheckBox cbCover = dialogView.findViewById(R.id.cbIsCover);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelImage);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnUploadImage);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "Image URL is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            String caption = etCaption.getText().toString().trim();
            boolean isCover = cbCover.isChecked();

            repository.addImage(businessId, url, caption, isCover, new BusinessRepository.RepositoryCallback<BusinessImage>() {
                @Override
                public void onSuccess(BusinessImage data) {
                    Toast.makeText(requireContext(), "Photo added to gallery!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    setupViewPager();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    // ==========================================
    // ViewPager Adapter
    // ==========================================
    static class ListingsPagerAdapter extends FragmentStateAdapter {
        public ListingsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new PromotionsTabFragment();
                case 2:
                    return new GalleryTabFragment();
                default:
                    return new ServicesTabFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    // ==========================================
    // 1. Services Tab Fragment
    // ==========================================
    public static class ServicesTabFragment extends Fragment {
        private SwipeRefreshLayout swipeRefresh;
        private RecyclerView recyclerView;
        private View emptyView;
        private ProgressBar progressBar;
        private BusinessServiceAdapter adapter;
        private BusinessRepository repo;
        private Long bId;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.page_business_services, container, false);
            swipeRefresh = v.findViewById(R.id.swipeRefreshServices);
            recyclerView = v.findViewById(R.id.rvServices);
            emptyView = v.findViewById(R.id.layoutEmptyServices);
            progressBar = v.findViewById(R.id.pbLoadingServices);
            return v;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            repo = new BusinessRepository(requireContext());
            Business b = new AppPreference(requireContext()).getSelectedBusiness();
            bId = b != null ? b.getId() : null;

            adapter = new BusinessServiceAdapter(requireContext(), new BusinessServiceAdapter.OnServiceActionListener() {
                @Override
                public void onEdit(BusinessService service) {
                    if (getParentFragment() instanceof BusinessListingsFragment) {
                        ((BusinessListingsFragment) getParentFragment()).showManageServiceDialog(service);
                    }
                }

                @Override
                public void onDelete(BusinessService service) {
                    confirmDelete(service);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

            swipeRefresh.setOnRefreshListener(this::loadServices);
            loadServices();
        }

        private void loadServices() {
            if (bId == null) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            repo.getServices(bId, new BusinessRepository.RepositoryCallback<List<BusinessService>>() {
                @Override
                public void onSuccess(List<BusinessService> data) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    if (data != null && !data.isEmpty()) {
                        adapter.setServices(data);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        adapter.setServices(null);
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }

        private void confirmDelete(BusinessService service) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete '" + service.getName() + "'?")
                    .setPositiveButton("Delete", (d, w) -> {
                        repo.deleteService(bId, service.getId(), new BusinessRepository.RepositoryCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(requireContext(), "Service deleted.", Toast.LENGTH_SHORT).show();
                                loadServices();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // ==========================================
    // 2. Promotions Tab Fragment
    // ==========================================
    public static class PromotionsTabFragment extends Fragment {
        private SwipeRefreshLayout swipeRefresh;
        private RecyclerView recyclerView;
        private View emptyView;
        private ProgressBar progressBar;
        private BusinessPromotionAdapter adapter;
        private BusinessRepository repo;
        private Long bId;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.page_business_promotions, container, false);
            swipeRefresh = v.findViewById(R.id.swipeRefreshPromotions);
            recyclerView = v.findViewById(R.id.rvPromotions);
            emptyView = v.findViewById(R.id.layoutEmptyPromotions);
            progressBar = v.findViewById(R.id.pbLoadingPromotions);
            return v;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            repo = new BusinessRepository(requireContext());
            Business b = new AppPreference(requireContext()).getSelectedBusiness();
            bId = b != null ? b.getId() : null;

            adapter = new BusinessPromotionAdapter(requireContext(), new BusinessPromotionAdapter.OnPromoActionListener() {
                @Override
                public void onEdit(BusinessPromotion promotion) {
                    if (getParentFragment() instanceof BusinessListingsFragment) {
                        ((BusinessListingsFragment) getParentFragment()).showManagePromoDialog(promotion);
                    }
                }

                @Override
                public void onDelete(BusinessPromotion promotion) {
                    confirmDelete(promotion);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);

            swipeRefresh.setOnRefreshListener(this::loadPromotions);
            loadPromotions();
        }

        private void loadPromotions() {
            if (bId == null) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            repo.getPromotions(bId, new BusinessRepository.RepositoryCallback<List<BusinessPromotion>>() {
                @Override
                public void onSuccess(List<BusinessPromotion> data) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    if (data != null && !data.isEmpty()) {
                        adapter.setPromotions(data);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        adapter.setPromotions(null);
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }

        private void confirmDelete(BusinessPromotion promo) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Promotion")
                    .setMessage("Are you sure you want to delete promo code '" + promo.getCode() + "'?")
                    .setPositiveButton("Delete", (d, w) -> {
                        repo.deletePromotion(bId, promo.getId(), new BusinessRepository.RepositoryCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(requireContext(), "Promotion deleted.", Toast.LENGTH_SHORT).show();
                                loadPromotions();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // ==========================================
    // 3. Gallery Tab Fragment
    // ==========================================
    public static class GalleryTabFragment extends Fragment {
        private SwipeRefreshLayout swipeRefresh;
        private RecyclerView recyclerView;
        private View emptyView;
        private ProgressBar progressBar;
        private BusinessGalleryAdapter adapter;
        private BusinessRepository repo;
        private Long bId;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.page_business_gallery, container, false);
            swipeRefresh = v.findViewById(R.id.swipeRefreshGallery);
            recyclerView = v.findViewById(R.id.rvGallery);
            emptyView = v.findViewById(R.id.layoutEmptyGallery);
            progressBar = v.findViewById(R.id.pbLoadingGallery);
            return v;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            repo = new BusinessRepository(requireContext());
            Business b = new AppPreference(requireContext()).getSelectedBusiness();
            bId = b != null ? b.getId() : null;

            adapter = new BusinessGalleryAdapter(requireContext(), this::confirmDelete);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            recyclerView.setAdapter(adapter);

            swipeRefresh.setOnRefreshListener(this::loadGallery);
            loadGallery();
        }

        private void loadGallery() {
            if (bId == null) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            repo.getImages(bId, new BusinessRepository.RepositoryCallback<List<BusinessImage>>() {
                @Override
                public void onSuccess(List<BusinessImage> data) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    if (data != null && !data.isEmpty()) {
                        adapter.setImages(data);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        adapter.setImages(null);
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }

        private void confirmDelete(BusinessImage image) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Image")
                    .setMessage("Remove this image from your business gallery?")
                    .setPositiveButton("Delete", (d, w) -> {
                        repo.deleteImage(bId, image.getId(), new BusinessRepository.RepositoryCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(requireContext(), "Image removed.", Toast.LENGTH_SHORT).show();
                                loadGallery();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}

