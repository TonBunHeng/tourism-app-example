package com.example.smarttourismapp.ui.business;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ItemBusinessBinding;
import com.example.smarttourismapp.model.Business;

import java.util.Objects;

public class BusinessAdapter extends ListAdapter<Business, BusinessAdapter.BusinessViewHolder> {

    public interface OnBusinessClickListener {
        void onBusinessClick(Business business);
    }

    private final OnBusinessClickListener listener;

    public BusinessAdapter(OnBusinessClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Business> DIFF_CALLBACK = new DiffUtil.ItemCallback<Business>() {
        @Override
        public boolean areItemsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getAddress(), newItem.getAddress());
        }
    };

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBusinessBinding binding = ItemBusinessBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new BusinessViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {
        private final ItemBusinessBinding binding;

        public BusinessViewHolder(@NonNull ItemBusinessBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Business business, OnBusinessClickListener listener) {
            if (business == null)
                return;

            binding.tvBusinessName.setText(business.getName());
            binding.tvBusinessCategory.setText(business.getCategoryString());
            binding.tvBusinessAddress
                    .setText(business.getAddress().isEmpty() ? business.getProvinceString() : business.getAddress());

            if (business.getImageUrl() != null && !business.getImageUrl().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(business.getImageUrl())
                        .placeholder(R.drawable.onboarding_2)
                        .error(R.drawable.onboarding_2)
                        .into(binding.ivBusinessImage);
            } else {
                binding.ivBusinessImage.setImageResource(R.drawable.onboarding_2);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null)
                    listener.onBusinessClick(business);
            });
        }
    }
}
