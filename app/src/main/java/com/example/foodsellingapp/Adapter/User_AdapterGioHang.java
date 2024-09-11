package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.ChangeNumberItemsListerner;
import com.example.foodsellingapp.ManagementCart;
import com.example.foodsellingapp.Model.GioHang;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.databinding.ListAdminMonBinding;
import com.example.foodsellingapp.databinding.ListUserShoppingcartBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User_AdapterGioHang extends RecyclerView.Adapter<User_AdapterGioHang.HolderGioHang> {
    private ManagementCart managementCart;
    private ArrayList<MonAn> arrMonAnTrongGioHang;
    ChangeNumberItemsListerner changeNumberItemsListerner;
    //constructor


    public User_AdapterGioHang(ManagementCart managementCart, ArrayList<MonAn> arrMonAnTrongGioHang, ChangeNumberItemsListerner changeNumberItemsListerner) {
        this.managementCart = managementCart;
        this.arrMonAnTrongGioHang = arrMonAnTrongGioHang;
        this.changeNumberItemsListerner = changeNumberItemsListerner;
    }

    @NonNull
    @Override
    public HolderGioHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new HolderGioHang(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_shoppingcart,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull HolderGioHang holder, int position) {
        MonAn monAn=arrMonAnTrongGioHang.get(position);
         holder.tenMon.setText(monAn.getTenMon());
         Integer giaBan=monAn.getGia()+monAn.getPhuThu();
         holder.giaMon.setText("$"+giaBan);
         holder.sl.setText("x"+ String.valueOf(monAn.getNumberinCart()));
         holder.slDat.setText(String.valueOf(monAn.getNumberinCart()));
        Picasso.get().load(arrMonAnTrongGioHang.get(position).getUrl()).into(holder.imgMon);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.removeItem(arrMonAnTrongGioHang, position, new ChangeNumberItemsListerner() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                     changeNumberItemsListerner.change();
                    }
                });
            }
        });
        holder.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.minusNumberItem(arrMonAnTrongGioHang, position, new ChangeNumberItemsListerner() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberItemsListerner.change();
                    }
                });
            }
        });
        holder.btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.plusNumberItem(arrMonAnTrongGioHang, position, new ChangeNumberItemsListerner() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberItemsListerner.change();
                    }
                });
            }
        });
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.tenMon.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrMonAnTrongGioHang.size();
    }

    class HolderGioHang extends RecyclerView.ViewHolder {
        ImageView imgMon, btnDelete;
        TextView  tenMon,giaMon,sl, btnGiam, btnTang,slDat;

        public HolderGioHang(@NonNull View itemView) {
            super(itemView);
            imgMon=itemView.findViewById(R.id.imgMon);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            tenMon=itemView.findViewById(R.id.tenMon);
            giaMon=itemView.findViewById(R.id.giaMon);
            sl=itemView.findViewById(R.id.sl);
            btnGiam=itemView.findViewById(R.id.btnGiam);
            btnTang=itemView.findViewById(R.id.btnTang);
            slDat=itemView.findViewById(R.id.slDat);

        }
    }
}

