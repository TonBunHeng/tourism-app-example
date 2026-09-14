package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.R;
import com.example.tourism_app.databinding.ItemCategoryBinding;
import com.example.tourism_app.model.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), position);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category, int position) {
            binding.tvCategoryName.setText(category.getName());
            binding.ivCategoryIcon.setImageResource(category.getIconResId());

            if (category.isSelected()) {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.on_primary));
                binding.ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.on_primary));
            } else {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.surface));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.on_surface));
                binding.ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            }

            binding.getRoot().setOnClickListener(v -> {
                for (Category cat : categories) cat.setSelected(false);
                category.setSelected(true);
                notifyDataSetChanged();
                if (listener != null) listener.onCategoryClick(category, position);
            });
        }
    }
}