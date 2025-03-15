package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.Category;
import com.example.foodsellingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Set category name
        holder.txtCateName.setText(category.getName());

        // Set click listener for Edit button
        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(category);
            }
        });

        // Set click listener for Delete button
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(category);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Interface for handling click events
    public interface OnCategoryClickListener {
        void onEditClick(Category category);   // Called when Edit button is clicked

        void onDeleteClick(Category category); // Called when Delete button is clicked

    }
    public void updateList(List<Category> newList) {
        this.categoryList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }
    class CategoryViewHolder extends RecyclerView.ViewHolder {
TextView txtCateName;
ImageView imgEdit, imgDelete;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCateName=itemView.findViewById(R.id.categoryName);
            imgDelete=itemView.findViewById(R.id.imgDelete);
            imgEdit=itemView.findViewById(R.id.imgEdit);

        }
    }
}
