package com.example.tourism_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tourism_app.databinding.ActivityCreateTripBinding;
import java.util.Calendar;

public class CreateTripActivity extends AppCompatActivity {

    private ActivityCreateTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarCreateTrip, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        binding.toolbarCreateTrip.setNavigationOnClickListener(v -> finish());

        binding.etStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.etEndDate.setOnClickListener(v -> showDatePicker(false));

        binding.btnSaveTrip.setOnClickListener(v -> {
            String title = binding.etTripName.getText().toString().trim();
            if (title.isEmpty()) {
                binding.tilTripName.setError("Please enter a trip name");
                return;
            }
            Toast.makeText(this, "Trip '" + title + "' saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (isStart) {
                binding.etStartDate.setText(dateStr);
            } else {
                binding.etEndDate.setText(dateStr);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}