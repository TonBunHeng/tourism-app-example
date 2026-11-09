package com.example.smarttourismapp.ui.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.data.repository.BusinessRepository;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.utils.AppPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BusinessReportsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ChipGroup chipGroupTimeframe;
    private TextView tvTotalRevenue, tvCompletedBookings, tvAvgValue;
    private TextView tvCompletedStat, tvConfirmedStat, tvPendingStat, tvCancelledStat;
    private LinearLayout layoutTopServices;
    private TextView tvTopServicesText, tvUniqueCustomers, tvRepeatRate;
    private ProgressBar pbLoading;
    private MaterialButton btnExport;

    private BusinessRepository repository;
    private AppPreference appPreference;
    private Long businessId;
    private String currentTimeframe = "this_month";
    private Map<String, Object> currentReportData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new BusinessRepository(this);
        appPreference = new AppPreference(this);

        Business b = appPreference.getSelectedBusiness();
        businessId = b != null ? b.getId() : null;

        initViews();
        setupFilters();
        loadReports();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarReports);
        chipGroupTimeframe = findViewById(R.id.chipGroupTimeframe);
        tvTotalRevenue = findViewById(R.id.tvReportTotalRevenue);
        tvCompletedBookings = findViewById(R.id.tvReportCompletedBookings);
        tvAvgValue = findViewById(R.id.tvReportAvgValue);

        tvCompletedStat = findViewById(R.id.tvCompletedStat);
        tvConfirmedStat = findViewById(R.id.tvConfirmedStat);
        tvPendingStat = findViewById(R.id.tvPendingStat);
        tvCancelledStat = findViewById(R.id.tvCancelledStat);

        layoutTopServices = findViewById(R.id.layoutTopServices);
        tvTopServicesText = findViewById(R.id.tvTopServicesText);
        tvUniqueCustomers = findViewById(R.id.tvUniqueCustomers);
        tvRepeatRate = findViewById(R.id.tvRepeatRate);

        pbLoading = findViewById(R.id.pbLoadingReports);
        btnExport = findViewById(R.id.btnExportReport);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnExport.setOnClickListener(v -> exportCsvReport());
    }

    private void setupFilters() {
        chipGroupTimeframe.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipToday) {
                currentTimeframe = "today";
            } else if (checkedId == R.id.chipThisWeek) {
                currentTimeframe = "this_week";
            } else if (checkedId == R.id.chipThisYear) {
                currentTimeframe = "this_year";
            } else {
                currentTimeframe = "this_month";
            }
            loadReports();
        });
    }

    private void loadReports() {
        pbLoading.setVisibility(View.VISIBLE);
        repository.getReports(currentTimeframe, businessId, new BusinessRepository.RepositoryCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                pbLoading.setVisibility(View.GONE);
                currentReportData = data;
                populateReportData(data);
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(BusinessReportsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void populateReportData(Map<String, Object> data) {
        if (data == null) return;

        // 1. Summary
        if (data.containsKey("summary") && data.get("summary") instanceof Map) {
            Map<String, Object> summary = (Map<String, Object>) data.get("summary");
            double rev = parseDouble(summary.get("total_revenue"));
            int completed = parseInt(summary.get("completed_bookings"));
            double avg = parseDouble(summary.get("average_booking_value"));

            tvTotalRevenue.setText(String.format(Locale.US, "$%.2f", rev));
            tvCompletedBookings.setText(String.valueOf(completed));
            tvAvgValue.setText(String.format(Locale.US, "$%.2f", avg));

            int confirmed = parseInt(summary.get("confirmed_bookings"));
            int pending = parseInt(summary.get("pending_bookings"));
            int cancelled = parseInt(summary.get("cancelled_bookings"));
            int total = parseInt(summary.get("total_bookings"));

            tvCompletedStat.setText(String.format(Locale.US, "• Completed: %d", completed));
            tvConfirmedStat.setText(String.format(Locale.US, "• Confirmed: %d", confirmed));
            tvPendingStat.setText(String.format(Locale.US, "• Pending Action: %d", pending));
            tvCancelledStat.setText(String.format(Locale.US, "• Cancelled / Rejected: %d", cancelled));
        }

        // 2. Customer Insights
        if (data.containsKey("customer_insights") && data.get("customer_insights") instanceof Map) {
            Map<String, Object> insights = (Map<String, Object>) data.get("customer_insights");
            int unique = parseInt(insights.get("total_unique_customers"));
            double repeatRate = parseDouble(insights.get("repeat_customer_rate"));

            tvUniqueCustomers.setText(String.format(Locale.US, "Unique Guests: %d", unique));
            tvRepeatRate.setText(String.format(Locale.US, "Repeat Customer Rate: %.1f%%", repeatRate));
        }

        // 3. Top Services
        if (data.containsKey("top_services") && data.get("top_services") instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("top_services");
            if (!list.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(list.size(), 5); i++) {
                    Map<String, Object> item = list.get(i);
                    String name = String.valueOf(item.get("service_name") != null ? item.get("service_name") : "Service");
                    double rev = parseDouble(item.get("revenue"));
                    int count = parseInt(item.get("bookings_count"));
                    sb.append(String.format(Locale.US, "%d. %s — $%.2f (%d bookings)\n", (i + 1), name, rev, count));
                }
                tvTopServicesText.setText(sb.toString().trim());
            } else {
                tvTopServicesText.setText("No offerings data recorded for this timeframe.");
            }
        }
    }

    private void exportCsvReport() {
        if (currentReportData == null) {
            Toast.makeText(this, "Report data is still loading.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File exportDir = new File(getCacheDir(), "reports");
            if (!exportDir.exists()) exportDir.mkdirs();
            File file = new File(exportDir, "business_report_" + currentTimeframe + ".csv");

            FileOutputStream fos = new FileOutputStream(file);
            StringBuilder sb = new StringBuilder();
            sb.append("Smart Tourism Cambodia - Business Performance Statement\n");
            sb.append("Timeframe,").append(currentTimeframe).append("\n");
            sb.append("Export Date,").append(new java.util.Date()).append("\n\n");

            sb.append("Metric,Value\n");
            sb.append("Total Revenue,").append(tvTotalRevenue.getText()).append("\n");
            sb.append("Completed Bookings,").append(tvCompletedBookings.getText()).append("\n");
            sb.append("Average Booking Value,").append(tvAvgValue.getText()).append("\n");
            sb.append(tvCompletedStat.getText().toString().replace("• ", "")).append("\n");
            sb.append(tvConfirmedStat.getText().toString().replace("• ", "")).append("\n");
            sb.append(tvPendingStat.getText().toString().replace("• ", "")).append("\n");
            sb.append(tvCancelledStat.getText().toString().replace("• ", "")).append("\n");

            fos.write(sb.toString().getBytes());
            fos.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Business Performance Statement");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share or Export Report"));

        } catch (Exception e) {
            Toast.makeText(this, "Failed to export report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private double parseDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble((String) val); } catch (Exception ignored) {}
        }
        return 0.0;
    }

    private int parseInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try { return Integer.parseInt((String) val); } catch (Exception ignored) {}
        }
        return 0;
    }
}

