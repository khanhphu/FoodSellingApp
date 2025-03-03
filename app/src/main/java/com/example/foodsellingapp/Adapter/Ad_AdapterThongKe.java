package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.StatisticItems;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.databinding.ListFilterBinding;

import java.util.ArrayList;
import java.util.List;

public class Ad_AdapterThongKe extends RecyclerView.Adapter<Ad_AdapterThongKe.ViewHolder> {
    private List<StatisticItems> itemsList = new ArrayList<>(); // Initialize to avoid null
    private Context context;
    private ListFilterBinding binding;

    public Ad_AdapterThongKe(List<StatisticItems> itemsList, Context context) {
        this.itemsList = itemsList != null ? itemsList : new ArrayList<>(); // Fallback to empty list if null
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (itemsList == null || position >= itemsList.size()) {
            Log.e("Adapter", "Invalid position or itemsList is null: position=" + position);
            return;
        }
        StatisticItems items = itemsList.get(position);
        Log.d("Adapter", "Binding item at position " + position + ": ID=" + items.getId() + ", Type=" + items.getType() + ", Status=" + items.getStatus());
        holder.Id.setText(items.getId() != null ? items.getId() : "No ID");
        holder.noiDung1.setText(items.getType() != null ? items.getType() : "No Type");

        switch (items.getType()) {
            case StatisticItems.TYPE_DONHANG:
                holder.noiDung2.setText("Status: " + (items.getStatus() != null ? items.getStatus() : "No Status"));

                break;
            case StatisticItems.TYPE_MONAN:
                holder.noiDung2.setText(items.getOrderCount() > 0 ?
                        "Orders: " + items.getOrderCount() :
                        "In Stock: " + items.getQuantity());
                break;
            case StatisticItems.TYPE_KH: // Fixed constant name
                holder.noiDung2.setText("Type: " + (items.getStatus() != null ? items.getStatus() : "No Status"));
                break;
            default:
                holder.noiDung2.setText(items.getName() != null ? items.getName() : "No Name");
                break;
        }
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "getItemCount: " + (itemsList != null ? itemsList.size() : 0));
        return itemsList != null ? itemsList.size() : 0;
    }

    public void updateList(List<StatisticItems> items) {
        if (items == null) {
            Log.e("Adapter", "updateList called with null items");
            return;
        }
        itemsList.clear();
        itemsList.addAll(items);
        Log.d("Adapter", "Updated list size: " + itemsList.size());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView noiDung1, noiDung2, Id;
        ImageView btn_close;

        public ViewHolder(ListFilterBinding binding) {
            super(binding.getRoot());
            this.noiDung1 = binding.txtNoiDung1;
            this.noiDung2 = binding.txtNoiDung2;
            this.Id = binding.txtID;
            this.btn_close = binding.btnClose;

            // Example: Add click listener using context
            btn_close.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemsList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Item removed at position " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}