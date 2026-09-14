package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.R;
import com.example.tourism_app.databinding.ItemPlaceBinding;
import com.example.tourism_app.model.Place;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
        void onFavoriteClick(Place place, int position);
    }

    private List<Place> places;
    private final OnPlaceClickListener listener;

    public PlaceAdapter(List<Place> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceBinding binding = ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaceBinding binding;

        PlaceViewHolder(ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Place place) {
            binding.tvPlaceName.setText(place.getName());
            binding.tvPlaceLocation.setText(place.getLocation());
            binding.tvPlaceCategory.setText(place.getCategory());
            binding.tvPlaceRating.setText(String.valueOf(place.getRating()));
            binding.tvPlacePrice.setText(place.getPrice());
            binding.ivPlaceImage.setImageResource(place.getImageResId());

            if (place.isFavorite()) {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onPlaceClick(place);
            });

            binding.btnFavorite.setOnClickListener(v -> {
                place.setFavorite(!place.isFavorite());
                notifyItemChanged(getAdapterPosition());
                if (listener != null) listener.onFavoriteClick(place, getAdapterPosition());
            });
        }
    }
}