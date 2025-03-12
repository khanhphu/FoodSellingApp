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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<Category> categoryList;

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.list_category,parent,false);
       return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
Category category=categoryList.get(position);
//holder.categoryName.setText(category.getName());
//        Picasso.get().load(category.getUrl()).into(holder.imgCategory);
//
}

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
TextView categoryName;
ImageView imgCategory;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName=itemView.findViewById(R.id.categoryName);
          //  imgCategory=itemView.findViewById(R.id.imgCategory);
        }
    }

}
