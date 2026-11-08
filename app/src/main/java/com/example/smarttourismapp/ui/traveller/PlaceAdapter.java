package com.example.smarttourismapp.ui.traveller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ItemPlaceBinding;
import com.example.smarttourismapp.model.Place;

import java.util.Locale;
import java.util.Objects;

public class PlaceAdapter extends ListAdapter<Place, PlaceAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
        void onFavoriteClick(Place place);
    }

    private final OnPlaceClickListener listener;

    public PlaceAdapter(OnPlaceClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Place> DIFF_CALLBACK = new DiffUtil.ItemCallback<Place>() {
        @Override
        public boolean areItemsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl()) &&
                    Objects.equals(oldItem.getPrice(), newItem.getPrice()) &&
                    oldItem.getRating() == newItem.getRating();
        }
    };

    public void setPlaces(java.util.List<Place> places) {
        submitList(places != null ? new java.util.ArrayList<>(places) : null);
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceBinding binding = ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaceBinding binding;

        public PlaceViewHolder(@NonNull ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Place place, OnPlaceClickListener listener) {
            if (place == null) return;

            binding.tvPlaceName.setText(place.getName());

            String category = place.getCategoryName();
            if (category == null || category.trim().isEmpty()) category = "Destination";
            binding.tvCategory.setText(category);

            String province = place.getProvinceName();
            if (province == null || province.trim().isEmpty()) province = "Cambodia";
            binding.tvLocation.setText(province);

            binding.tvRating.setText(String.format(Locale.US, "%.1f", place.getRating() > 0 ? place.getRating() : 4.5f));

            if (place.getBestTime() != null && !place.getBestTime().trim().isEmpty()) {
                binding.tvBestTime.setText("Best: " + place.getBestTime());
            } else {
                binding.tvBestTime.setText("Open all year");
            }

            if (place.getPrice() != null && !place.getPrice().trim().isEmpty()) {
                binding.tvPrice.setText(place.getPrice());
            } else {
                binding.tvPrice.setText("Free Entry");
            }

            if (place.getImageUrl() != null && !place.getImageUrl().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(place.getImageUrl())
                        .placeholder(R.drawable.onboarding_1)
                        .error(R.drawable.onboarding_1)
                        .into(binding.ivPlaceImage);
            } else {
                binding.ivPlaceImage.setImageResource(R.drawable.onboarding_1);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onPlaceClick(place);
            });

            binding.btnFavorite.setOnClickListener(v -> {
                if (listener != null) listener.onFavoriteClick(place);
            });
        }
    }
}
