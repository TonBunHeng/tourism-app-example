package com.example.smarttourismapp.ui.business;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.Review;
import com.example.smarttourismapp.ui.business.adapter.BusinessReviewAdapter;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessReviewsFragment extends Fragment {

    private TextView tvAverageRatingValue, tvTotalReviewsCount;
    private RatingBar ratingBarHeader;
    private ChipGroup chipGroupRating;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View emptyView;
    private ProgressBar progressBar;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private BusinessReviewAdapter adapter;

    private Long businessId;
    private int selectedStarFilter = 0; // 0 = all
    private final List<Review> allReviews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_reviews, container, false);
        initViews(view);
        setupAdapter();
        setupFilters();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new BusinessRepository(requireContext());
        appPreference = new AppPreference(requireContext());

        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;

        loadReviews();
    }

    @Override
    public void onResume() {
        super.onResume();
        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;
        loadReviews();
    }

    private void initViews(View view) {
        tvAverageRatingValue = view.findViewById(R.id.tvAverageRatingValue);
        tvTotalReviewsCount = view.findViewById(R.id.tvTotalReviewsCount);
        ratingBarHeader = view.findViewById(R.id.ratingBarHeader);
        chipGroupRating = view.findViewById(R.id.chipGroupReviewRating);
        swipeRefresh = view.findViewById(R.id.swipeRefreshReviews);
        recyclerView = view.findViewById(R.id.rvReviews);
        emptyView = view.findViewById(R.id.layoutEmptyReviews);
        progressBar = view.findViewById(R.id.pbLoadingReviews);
    }

    private void setupAdapter() {
        adapter = new BusinessReviewAdapter(requireContext(), this::showReplyDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadReviews);
    }

    private void setupFilters() {
        chipGroupRating.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipReview5) {
                selectedStarFilter = 5;
            } else if (checkedId == R.id.chipReview4) {
                selectedStarFilter = 4;
            } else if (checkedId == R.id.chipReview3) {
                selectedStarFilter = 3;
            } else if (checkedId == R.id.chipReview2) {
                selectedStarFilter = 2;
            } else if (checkedId == R.id.chipReview1) {
                selectedStarFilter = 1;
            } else {
                selectedStarFilter = 0;
            }
            applyRatingFilter();
        });
    }

    public void loadReviews() {
        if (businessId == null) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        repository.getReviews(businessId, new BusinessRepository.RepositoryCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                allReviews.clear();
                if (data != null) allReviews.addAll(data);

                updateHeaderSummary();
                applyRatingFilter();
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

    private void updateHeaderSummary() {
        if (allReviews.isEmpty()) {
            tvAverageRatingValue.setText("5.0");
            ratingBarHeader.setRating(5.0f);
            tvTotalReviewsCount.setText("No reviews received yet");
            return;
        }

        float total = 0;
        for (Review r : allReviews) total += r.getRating();
        float avg = total / allReviews.size();

        tvAverageRatingValue.setText(String.format(Locale.US, "%.1f", avg));
        ratingBarHeader.setRating(avg);
        tvTotalReviewsCount.setText(String.format(Locale.US, "Based on %d verified customer reviews", allReviews.size()));
    }

    private void applyRatingFilter() {
        List<Review> filtered = new ArrayList<>();
        for (Review r : allReviews) {
            if (selectedStarFilter == 0 || (int) Math.floor(r.getRating()) == selectedStarFilter) {
                filtered.add(r);
            }
        }

        adapter.setReviews(filtered);
        if (filtered.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showReplyDialog(Review review) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reply_review, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvQuote = dialogView.findViewById(R.id.tvCustomerReviewQuote);
        EditText etReply = dialogView.findViewById(R.id.etReplyText);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelReply);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmitReply);

        tvQuote.setText("\"" + review.getComment() + "\"");
        if (review.getOwnerReply() != null) {
            etReply.setText(review.getOwnerReply());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String text = etReply.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Please write a response.", Toast.LENGTH_SHORT).show();
                return;
            }

            repository.replyReview(businessId, review.getId(), text, new BusinessRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(requireContext(), "Reply sent to customer!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadReviews();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}

