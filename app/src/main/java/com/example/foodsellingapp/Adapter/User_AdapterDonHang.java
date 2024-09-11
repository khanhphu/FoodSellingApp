package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.databinding.ListUserDonhangBinding;

import java.util.ArrayList;

public class User_AdapterDonHang extends RecyclerView.Adapter<User_AdapterDonHang.HolderDonHang> {
private Context context;
private ArrayList<CTDonHang> arrDH;
private ListUserDonhangBinding binding;
    public User_AdapterDonHang(Context context, ArrayList<CTDonHang> arrDH) {
        this.context = context;
        this.arrDH = arrDH;
    }

    @NonNull
    @Override
    public HolderDonHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= ListUserDonhangBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderDonHang(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDonHang holder, int position) {
        CTDonHang dh=arrDH.get(position);
        holder.dhTenMon.setText(dh.getTenMon());
        holder.dhSL.setText(""+dh.getSl()+"X");
        holder.dhGia.setText(""+dh.getGia());
    }

    @Override
    public int getItemCount() {
        return arrDH.size();
    }

    class HolderDonHang extends RecyclerView.ViewHolder {
    TextView dhTenMon, dhSL,dhGia;
    public HolderDonHang(@NonNull View itemView) {
        super(itemView);
        dhGia=binding.dhGia;
        dhSL=binding.dhSL;
        dhTenMon=binding.dhTenMon;

    }
}
}
