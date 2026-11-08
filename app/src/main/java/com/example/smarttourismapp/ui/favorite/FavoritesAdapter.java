package com.example.smarttourismapp.ui.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ItemFavoriteBinding;
import com.example.smarttourismapp.model.FavoriteItem;
import com.example.smarttourismapp.model.Place;

import java.util.Objects;

public class FavoritesAdapter extends ListAdapter<FavoriteItem, FavoritesAdapter.FavoriteViewHolder> {

    public interface OnFavoriteActionListener {
        void onPlaceClick(Place place);

        void onToggleVisited(FavoriteItem item);

        void onRemoveFavorite(FavoriteItem item);
    }

    private final OnFavoriteActionListener listener;

    public FavoritesAdapter(OnFavoriteActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FavoriteItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FavoriteItem oldItem, @NonNull FavoriteItem newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavoriteItem oldItem, @NonNull FavoriteItem newItem) {
            return oldItem.isVisited() == newItem.isVisited() &&
                    Objects.equals(oldItem.getPlaceId(), newItem.getPlaceId());
        }
    };

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public FavoriteViewHolder(@NonNull ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavoriteItem item, OnFavoriteActionListener listener) {
            if (item == null)
                return;
            Place place = item.getPlace();
            if (place == null)
                return;

            binding.tvFavName.setText(place.getName());
            binding.tvFavCategory.setText(place.getCategoryName().isEmpty() ? "Destination" : place.getCategoryName());
            binding.tvFavLocation.setText(place.getProvinceName().isEmpty() ? "Cambodia" : place.getProvinceName());

            if (item.isVisited()) {
                binding.tvFavVisitedBadge.setVisibility(View.VISIBLE);
                binding.btnToggleVisited.setText("Visited");
            } else {
                binding.tvFavVisitedBadge.setVisibility(View.GONE);
                binding.btnToggleVisited.setText("Mark Visited");
            }

            if (place.getImageUrl() != null && !place.getImageUrl().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(place.getImageUrl())
                        .placeholder(R.drawable.onboarding_1)
                        .error(R.drawable.onboarding_1)
                        .into(binding.ivFavImage);
            } else {
                binding.ivFavImage.setImageResource(R.drawable.onboarding_1);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null)
                    listener.onPlaceClick(place);
            });

            binding.btnToggleVisited.setOnClickListener(v -> {
                if (listener != null)
                    listener.onToggleVisited(item);
            });

            binding.btnFavDelete.setOnClickListener(v -> {
                if (listener != null)
                    listener.onRemoveFavorite(item);
            });
        }
    }
}
