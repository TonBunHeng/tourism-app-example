package com.example.smarttourismapp.ui.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;
import com.example.smarttourismapp.model.BusinessPromotion;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessPromotionAdapter extends RecyclerView.Adapter<BusinessPromotionAdapter.PromoViewHolder> {

    public interface OnPromoActionListener {
        void onEdit(BusinessPromotion promotion);
        void onDelete(BusinessPromotion promotion);
    }

    private final Context context;
    private final List<BusinessPromotion> promoList = new ArrayList<>();
    private final OnPromoActionListener listener;

    public BusinessPromotionAdapter(Context context, OnPromoActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setPromotions(List<BusinessPromotion> list) {
        promoList.clear();
        if (list != null) {
            promoList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_promotion, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        BusinessPromotion promo = promoList.get(position);
        holder.tvPromoTitle.setText(promo.getTitle());
        holder.tvPromoDiscount.setText(String.format(Locale.US, "%.0f%% OFF", promo.getDiscountPercent()));
        holder.tvPromoCode.setText("CODE: " + promo.getCode());

        String dates = "";
        if (!promo.getStartDate().isEmpty() || !promo.getEndDate().isEmpty()) {
            dates = "Valid: " + promo.getStartDate() + " - " + promo.getEndDate();
        }
        holder.tvPromoDates.setText(dates);

        if (promo.getDescription() != null && !promo.getDescription().isEmpty()) {
            holder.tvPromoDescription.setText(promo.getDescription());
            holder.tvPromoDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvPromoDescription.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(promo);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(promo);
        });
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromoTitle, tvPromoDiscount, tvPromoCode, tvPromoDates, tvPromoDescription;
        MaterialButton btnEdit, btnDelete;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPromoTitle = itemView.findViewById(R.id.tvPromoTitle);
            tvPromoDiscount = itemView.findViewById(R.id.tvPromoDiscount);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            tvPromoDates = itemView.findViewById(R.id.tvPromoDates);
            tvPromoDescription = itemView.findViewById(R.id.tvPromoDescription);
            btnEdit = itemView.findViewById(R.id.btnEditPromo);
            btnDelete = itemView.findViewById(R.id.btnDeletePromo);
        }
    }
}

