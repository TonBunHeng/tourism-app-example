package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.BusinessService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessServiceAdapter extends RecyclerView.Adapter<BusinessServiceAdapter.ServiceViewHolder> {

    public interface OnServiceActionListener {
        void onEdit(BusinessService service);
        void onDelete(BusinessService service);
    }

    private final Context context;
    private final List<BusinessService> serviceList = new ArrayList<>();
    private final OnServiceActionListener listener;

    public BusinessServiceAdapter(Context context, OnServiceActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setServices(List<BusinessService> services) {
        serviceList.clear();
        if (services != null) {
            serviceList.addAll(services);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        BusinessService service = serviceList.get(position);
        holder.tvServiceName.setText(service.getName());
        holder.tvServicePrice.setText(String.format(Locale.US, "$%.2f", service.getPrice()));

        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            holder.tvServiceDescription.setText(service.getDescription());
            holder.tvServiceDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvServiceDescription.setVisibility(View.GONE);
        }

        holder.tvServiceDuration.setText("🕒 " + service.getDuration());
        holder.tvServiceCapacity.setText(String.format(Locale.US, "👥 Up to %d guests", service.getCapacity()));

        if (service.isActive()) {
            holder.chipStatus.setText("Active");
            holder.chipStatus.setChipBackgroundColorResource(R.color.success);
        } else {
            holder.chipStatus.setText("Inactive");
            holder.chipStatus.setChipBackgroundColorResource(R.color.mute);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(service);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(service);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServicePrice, tvServiceDescription, tvServiceDuration, tvServiceCapacity;
        Chip chipStatus;
        MaterialButton btnEdit, btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvServiceCapacity = itemView.findViewById(R.id.tvServiceCapacity);
            chipStatus = itemView.findViewById(R.id.chipServiceStatus);
            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}

