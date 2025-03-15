package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.Users;
import com.example.foodsellingapp.databinding.ListAdDsachkhBinding;

import java.util.ArrayList;

public class Ad_AdapterUsers extends RecyclerView.Adapter<Ad_AdapterUsers.HolderUsers> {
    //context
    private Context context;
    //array list
    ArrayList<Users> arr_users;
    //view binding
    private ListAdDsachkhBinding dsachkhBinding;
    //constructor
    public Ad_AdapterUsers(Context context, ArrayList<Users> arr_users) {
        this.context = context;
        this.arr_users = arr_users;
    }

    @NonNull
    @Override
    public HolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       dsachkhBinding=ListAdDsachkhBinding.inflate(LayoutInflater.from(context),parent,false);
       return new HolderUsers(dsachkhBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUsers holder, int position) {
    //get data, set data, handle clicks,...
        //get data
        Users usersModel=arr_users.get(position);
        String cusName=usersModel.getName();
        String cusEmail=usersModel.getEmail();
        //xu ly so don hang sau

        //set data
        holder.txtCusName.setText(cusName);
        holder.txtCusMail.setText(cusEmail);
        holder.txtSLDonHang.setText("30");//chua xu ly
//        int url=usersModel.getProfileImage()
    }

    @Override
    public int getItemCount() {
        return arr_users.size();
    }

    //view holder for  list_ad_dsachkh
    class HolderUsers extends RecyclerView.ViewHolder{
        //ui view for list_ad_dskhach_hang
        TextView txtCusName;
        TextView txtCusMail;
        TextView txtSLDonHang;
        ImageView imgCus;

        public HolderUsers(@NonNull View itemView) {
            super(itemView);
            txtSLDonHang=dsachkhBinding.txtSLDonHang;
            txtCusMail=dsachkhBinding.txtCusMail;
            txtCusName=dsachkhBinding.txtCusName;
        }
    }

}
