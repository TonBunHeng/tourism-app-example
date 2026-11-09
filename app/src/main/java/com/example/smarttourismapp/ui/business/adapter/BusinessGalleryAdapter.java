package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.BusinessImage;

import java.util.ArrayList;
import java.util.List;

public class BusinessGalleryAdapter extends RecyclerView.Adapter<BusinessGalleryAdapter.GalleryViewHolder> {

    public interface OnImageDeleteListener {
        void onDelete(BusinessImage image);
    }

    private final Context context;
    private final List<BusinessImage> imageList = new ArrayList<>();
    private final OnImageDeleteListener deleteListener;

    public BusinessGalleryAdapter(Context context, OnImageDeleteListener deleteListener) {
        this.context = context;
        this.deleteListener = deleteListener;
    }

    public void setImages(List<BusinessImage> list) {
        imageList.clear();
        if (list != null) {
            imageList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        BusinessImage img = imageList.get(position);

        Glide.with(context)
                .load(img.getImageUrl())
                .placeholder(R.drawable.splash_background)
                .error(R.drawable.splash_background)
                .centerCrop()
                .into(holder.ivGalleryImage);

        if (img.getCaption() != null && !img.getCaption().isEmpty()) {
            holder.tvCaption.setText(img.getCaption());
            holder.tvCaption.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaption.setVisibility(View.GONE);
        }

        holder.tvCoverBadge.setVisibility(img.isCover() ? View.VISIBLE : View.GONE);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(img);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGalleryImage;
        TextView tvCaption, tvCoverBadge;
        ImageButton btnDelete;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.ivGalleryImage);
            tvCaption = itemView.findViewById(R.id.tvGalleryCaption);
            tvCoverBadge = itemView.findViewById(R.id.tvCoverBadge);
            btnDelete = itemView.findViewById(R.id.btnDeleteImage);
        }
    }
}

