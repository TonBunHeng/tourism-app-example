package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.Review;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BusinessReviewAdapter extends RecyclerView.Adapter<BusinessReviewAdapter.ReviewViewHolder> {

    public interface OnReviewReplyListener {
        void onReply(Review review);
    }

    private final Context context;
    private final List<Review> reviewList = new ArrayList<>();
    private final OnReviewReplyListener replyListener;

    public BusinessReviewAdapter(Context context, OnReviewReplyListener replyListener) {
        this.context = context;
        this.replyListener = replyListener;
    }

    public void setReviews(List<Review> list) {
        reviewList.clear();
        if (list != null) {
            reviewList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        String name = review.getUser() != null ? review.getUser().getName() : "Traveler";
        holder.tvReviewerName.setText(name);

        String initial = name.isEmpty() ? "T" : name.substring(0, 1).toUpperCase();
        holder.tvReviewerInitial.setText(initial);

        holder.tvReviewDate.setText(review.getCreatedAt());
        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewComment.setText(review.getComment());

        String reply = review.getOwnerReply();
        if (reply != null && !reply.trim().isEmpty()) {
            holder.layoutOwnerReply.setVisibility(View.VISIBLE);
            holder.tvOwnerReplyContent.setText(reply);
            holder.btnReply.setText("Edit Reply");
        } else {
            holder.layoutOwnerReply.setVisibility(View.GONE);
            holder.btnReply.setText("Reply");
        }

        holder.btnReply.setOnClickListener(v -> {
            if (replyListener != null) replyListener.onReply(review);
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerInitial, tvReviewerName, tvReviewDate, tvReviewComment, tvOwnerReplyContent;
        RatingBar ratingBar;
        LinearLayout layoutOwnerReply;
        MaterialButton btnReply;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerInitial = itemView.findViewById(R.id.tvReviewerInitial);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvOwnerReplyContent = itemView.findViewById(R.id.tvOwnerReplyContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            layoutOwnerReply = itemView.findViewById(R.id.layoutOwnerReply);
            btnReply = itemView.findViewById(R.id.btnReplyReview);
        }
    }
}

