package com.example.smarttourismapp.ui.business.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.BusinessHour;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessHourAdapter extends RecyclerView.Adapter<BusinessHourAdapter.HourViewHolder> {

    private final Context context;
    private final List<BusinessHour> hoursList = new ArrayList<>();

    public BusinessHourAdapter(Context context) {
        this.context = context;
    }

    public void setHours(List<BusinessHour> list) {
        hoursList.clear();
        if (list != null) {
            hoursList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<BusinessHour> getHours() {
        return hoursList;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_hour, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        BusinessHour hour = hoursList.get(position);

        holder.tvDayName.setText(hour.getDay());
        holder.btnOpenTime.setText(hour.getOpenTime());
        holder.btnCloseTime.setText(hour.getCloseTime());

        holder.switchClosed.setOnCheckedChangeListener(null);
        holder.switchClosed.setChecked(hour.isClosed());

        updateButtonsState(holder, hour.isClosed());

        holder.switchClosed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hour.setClosed(isChecked);
            updateButtonsState(holder, isChecked);
        });

        holder.btnOpenTime.setOnClickListener(v -> showTimePicker(hour.getOpenTime(), (h, m) -> {
            String formatted = String.format(Locale.US, "%02d:%02d", h, m);
            hour.setOpenTime(formatted);
            holder.btnOpenTime.setText(formatted);
        }));

        holder.btnCloseTime.setOnClickListener(v -> showTimePicker(hour.getCloseTime(), (h, m) -> {
            String formatted = String.format(Locale.US, "%02d:%02d", h, m);
            hour.setCloseTime(formatted);
            holder.btnCloseTime.setText(formatted);
        }));
    }

    private void updateButtonsState(HourViewHolder holder, boolean isClosed) {
        holder.btnOpenTime.setEnabled(!isClosed);
        holder.btnCloseTime.setEnabled(!isClosed);
        holder.btnOpenTime.setAlpha(isClosed ? 0.4f : 1.0f);
        holder.btnCloseTime.setAlpha(isClosed ? 0.4f : 1.0f);
    }

    private void showTimePicker(String currentTime, TimeSelectedListener listener) {
        int hour = 8;
        int minute = 0;
        try {
            if (currentTime != null && currentTime.contains(":")) {
                String[] parts = currentTime.split(":");
                hour = Integer.parseInt(parts[0].trim());
                minute = Integer.parseInt(parts[1].trim());
            }
        } catch (Exception ignored) {
        }

        new TimePickerDialog(context, (view, hourOfDay, min) -> {
            if (listener != null) listener.onTimeSelected(hourOfDay, min);
        }, hour, minute, true).show();
    }

    @Override
    public int getItemCount() {
        return hoursList.size();
    }

    interface TimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }

    static class HourViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        MaterialButton btnOpenTime, btnCloseTime;
        SwitchMaterial switchClosed;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            btnOpenTime = itemView.findViewById(R.id.btnOpenTime);
            btnCloseTime = itemView.findViewById(R.id.btnCloseTime);
            switchClosed = itemView.findViewById(R.id.switchClosed);
        }
    }
}

