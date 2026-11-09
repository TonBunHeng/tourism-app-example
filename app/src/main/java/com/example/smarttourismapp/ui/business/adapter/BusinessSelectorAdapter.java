package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.Business;

import java.util.ArrayList;
import java.util.List;

public class BusinessSelectorAdapter extends RecyclerView.Adapter<BusinessSelectorAdapter.BusinessViewHolder> {

    public interface OnBusinessSelectedListener {
        void onSelected(Business business);
    }

    private final Context context;
    private final List<Business> businessList = new ArrayList<>();
    private Long selectedBusinessId = null;
    private final OnBusinessSelectedListener listener;

    public BusinessSelectorAdapter(Context context, Long activeId, OnBusinessSelectedListener listener) {
        this.context = context;
        this.selectedBusinessId = activeId;
        this.listener = listener;
    }

    public void setBusinesses(List<Business> list, Long activeId) {
        businessList.clear();
        if (list != null) {
            businessList.addAll(list);
        }
        this.selectedBusinessId = activeId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_selector, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        Business biz = businessList.get(position);

        holder.tvName.setText(biz.getName());
        holder.tvCategory.setText(biz.getCategory() + " • " + biz.getProvince());

        if (biz.isVerified()) {
            holder.tvStatus.setText("Verified");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#DCFCE7"));
            holder.tvStatus.setTextColor(Color.parseColor("#166534"));
        } else {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FEF3C7"));
            holder.tvStatus.setTextColor(Color.parseColor("#92400E"));
        }

        boolean isSelected = selectedBusinessId != null && selectedBusinessId.equals(biz.getId());
        holder.ivCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            selectedBusinessId = biz.getId();
            notifyDataSetChanged();
            if (listener != null) listener.onSelected(biz);
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvStatus;
        ImageView ivCheck;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSelectorBusinessName);
            tvCategory = itemView.findViewById(R.id.tvSelectorBusinessCategory);
            tvStatus = itemView.findViewById(R.id.tvSelectorStatus);
            ivCheck = itemView.findViewById(R.id.ivSelectedCheck);
        }
    }
}

