package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.R;
import com.example.tourism_app.databinding.ItemTripBinding;
import com.example.tourism_app.model.Trip;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final List<Trip> trips;
    private final OnTripClickListener listener;

    public TripAdapter(List<Trip> trips, OnTripClickListener listener) {
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripBinding binding = ItemTripBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips != null ? trips.size() : 0;
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        private final ItemTripBinding binding;

        TripViewHolder(ItemTripBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Trip trip) {
            binding.tvTripTitle.setText(trip.getTitle());
            binding.tvTripDestination.setText(trip.getDestination());
            binding.tvTripDates.setText(trip.getDates());
            binding.tvTripStatus.setText(trip.getStatus());
            binding.ivTripThumb.setImageResource(trip.getImageResId());

            if ("Completed".equalsIgnoreCase(trip.getStatus())) {
                binding.tvTripStatus.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.status_completed_bg));
                binding.tvTripStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed_text));
            } else {
                binding.tvTripStatus.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.status_upcoming_bg));
                binding.tvTripStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_upcoming_text));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onTripClick(trip);
            });
        }
    }
}