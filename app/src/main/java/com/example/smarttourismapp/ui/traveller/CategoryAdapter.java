package com.example.smarttourismapp.ui.traveller;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.databinding.ItemCategoryBinding;
import com.example.smarttourismapp.model.Category;

import java.util.Objects;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName());
        }
    };

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        if (parent instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) parent;
            RecyclerView.LayoutManager lm = rv.getLayoutManager();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) binding.getRoot().getLayoutParams();
            if (lp != null) {
                if (lm instanceof GridLayoutManager) {
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int dp6 = (int) (6 * parent.getContext().getResources().getDisplayMetrics().density);
                    lp.setMargins(dp6, dp6, dp6, dp6);
                } else if (lm instanceof LinearLayoutManager && ((LinearLayoutManager) lm).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    int dp10 = (int) (10 * parent.getContext().getResources().getDisplayMetrics().density);
                    int dp4 = (int) (4 * parent.getContext().getResources().getDisplayMetrics().density);
                    lp.setMargins(0, dp4, dp10, dp4);
                }
                binding.getRoot().setLayoutParams(lp);
            }
        }

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            if (category == null) return;
            binding.tvCategoryName.setText(category.getName());
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(category);
            });
        }
    }
}

