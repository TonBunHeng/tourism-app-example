package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.databinding.ItemPlaceFeaturedBinding;
import com.example.tourism_app.model.Place;
import java.util.List;

public class FeaturedPlaceAdapter extends RecyclerView.Adapter<FeaturedPlaceAdapter.FeaturedViewHolder> {

    public interface OnFeaturedClickListener {
        void onFeaturedClick(Place place);
    }

    private final List<Place> places;
    private final OnFeaturedClickListener listener;

    public FeaturedPlaceAdapter(List<Place> places, OnFeaturedClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceFeaturedBinding binding = ItemPlaceFeaturedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new FeaturedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

    class FeaturedViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaceFeaturedBinding binding;

        FeaturedViewHolder(ItemPlaceFeaturedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Place place) {
            binding.tvFeaturedTitle.setText(place.getName());
            String subtitle = place.getRating() + " ★ • " + place.getLocation();
            binding.tvFeaturedSubtitle.setText(subtitle);
            binding.tvFeaturedTag.setText(place.getCategory());
            binding.ivFeaturedImage.setImageResource(place.getImageResId());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onFeaturedClick(place);
            });
        }
    }
}