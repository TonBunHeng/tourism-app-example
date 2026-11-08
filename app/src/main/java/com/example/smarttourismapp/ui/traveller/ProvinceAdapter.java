package com.example.smarttourismapp.ui.traveller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ItemProvinceBinding;
import com.example.smarttourismapp.model.Province;

import java.util.Objects;

public class ProvinceAdapter extends ListAdapter<Province, ProvinceAdapter.ProvinceViewHolder> {

    public interface OnProvinceClickListener {
        void onProvinceClick(Province province);
    }

    private final OnProvinceClickListener listener;

    public ProvinceAdapter(OnProvinceClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Province> DIFF_CALLBACK = new DiffUtil.ItemCallback<Province>() {
        @Override
        public boolean areItemsTheSame(@NonNull Province oldItem, @NonNull Province newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Province oldItem, @NonNull Province newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    };

    @NonNull
    @Override
    public ProvinceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProvinceBinding binding = ItemProvinceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ProvinceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProvinceViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ProvinceViewHolder extends RecyclerView.ViewHolder {
        private final ItemProvinceBinding binding;

        public ProvinceViewHolder(@NonNull ItemProvinceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Province province, OnProvinceClickListener listener) {
            if (province == null) return;
            binding.tvProvinceName.setText(province.getName());

            if (province.getImageUrl() != null && !province.getImageUrl().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(province.getImageUrl())
                        .placeholder(R.drawable.onboarding_1)
                        .error(R.drawable.onboarding_1)
                        .into(binding.ivProvinceImage);
            } else {
                binding.ivProvinceImage.setImageResource(R.drawable.onboarding_1);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onProvinceClick(province);
            });
        }
    }
}

