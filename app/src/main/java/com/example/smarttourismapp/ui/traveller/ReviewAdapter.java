package com.example.smarttourismapp.ui.traveller;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.databinding.ItemReviewBinding;
import com.example.smarttourismapp.model.Review;

import java.util.Locale;
import java.util.Objects;

public class ReviewAdapter extends ListAdapter<Review, ReviewAdapter.ReviewViewHolder> {

    public ReviewAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Review> DIFF_CALLBACK = new DiffUtil.ItemCallback<Review>() {
        @Override
        public boolean areItemsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getComment(), newItem.getComment()) &&
                    oldItem.getRating() == newItem.getRating();
        }
    };

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemReviewBinding binding;

        public ReviewViewHolder(@NonNull ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Review review) {
            if (review == null) return;

            String userName = review.getUser() != null ? review.getUser().getName() : "Traveler";
            binding.tvReviewUserName.setText(userName);

            binding.tvReviewRating.setText(String.format(Locale.US, "%.1f", review.getRating()));

            if (!TextUtils.isEmpty(review.getTitle())) {
                binding.tvReviewTitle.setText(review.getTitle());
                binding.tvReviewTitle.setVisibility(View.VISIBLE);
            } else {
                binding.tvReviewTitle.setVisibility(View.GONE);
            }

            binding.tvReviewComment.setText(review.getComment() != null ? review.getComment() : "");

            if (!TextUtils.isEmpty(review.getCreatedAt())) {
                binding.tvReviewDate.setText(review.getCreatedAt().substring(0, Math.min(10, review.getCreatedAt().length())));
            } else {
                binding.tvReviewDate.setText("Recent review");
            }

            if (review.getUser() != null && !TextUtils.isEmpty(review.getUser().getAvatar())) {
                Glide.with(itemView.getContext())
                        .load(review.getUser().getAvatar())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(binding.ivReviewUserAvatar);
            } else {
                binding.ivReviewUserAvatar.setImageResource(R.drawable.ic_person);
            }
        }
    }
}
