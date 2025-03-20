package com.example.foodsellingapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Format;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.OrderDetailActivity;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.databinding.ListUserDsachdonhangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


//adpater nay dung cho ca admin va khach hang
public class User_AdapterDSDonHang extends RecyclerView.Adapter<User_AdapterDSDonHang.HolderDSDH> {
    private ListUserDsachdonhangBinding binding;
    private String ngayXacNhanDon;
    private Context context;
    private ArrayList<DonHang> arrayList;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    //constructor

    public User_AdapterDSDonHang(Context context, ArrayList<DonHang> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HolderDSDH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListUserDsachdonhangBinding.inflate(LayoutInflater.from(context), parent, false);

        return new User_AdapterDSDonHang.HolderDSDH(binding.getRoot());
    }

    String userType;

    @Override
    public void onBindViewHolder(@NonNull HolderDSDH holder, int position) {

        DonHang donHang = arrayList.get(position);
        switch (donHang.getTrangThai()) {
            case "XacNhan":
                holder.txtTrangThai.setText("Xác nhận");
                holder.txtTrangThai.setBackgroundResource(R.drawable.status_confirm_background);
                //    holder.btnXacNhan.setVisibility(View.INVISIBLE);   VIEW DETAIL
                //    holder.btnDelete.setVisibility(View.INVISIBLE);
                holder.txtTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                holder.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
                break;
            case "choXacNhan":
                holder.txtTrangThai.setText("Chờ xác nhận");
                holder.txtTrangThai.setBackgroundResource(R.drawable.status_background);
                holder.txtTrangThai.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                holder.txtNgayXacNhan.setVisibility(View.GONE);
                break;
            case "Huy":
                holder.txtTrangThai.setText("Hủy");
                holder.txtTrangThai.setBackgroundResource(R.drawable.status_cancelled_background);
                holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                holder.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
                holder.txtTrangThai.setTextColor(context.getResources().getColor(android.R.color.white));

                break;
        }

        holder.btnViewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("donHang", (Serializable) donHang);
            context.startActivity(intent);
        });
        holder.txtTong.setText(Format.formatVND(donHang.getTongCong()));
        //load tenKH from maKH
        holder.txtMaDH.setText(donHang.getMaDH());
        holder.txtNgayDat.setText(donHang.getNgayTao());
        //loadMaKH(donHang,holder);
        loaduT(donHang);


    }


    private void loaduT(DonHang donHang) {

        DocumentReference documentReference = firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType1 = documentSnapshot.getString("userType");
                convertUserType(userType1, userType);
            }

        });

    }

    private void convertUserType(String userType1, String userType) {
        userType = userType1;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderDSDH extends RecyclerView.ViewHolder {
        TextView txtMaDH, txtTenKH, txtNgayDat, txtTrangThai, txtTong, txtNgayXacNhan, txtReason;
        ImageView btnXacNhan, btnDelete;
        Button btnViewDetail;

        public HolderDSDH(@NonNull View itemView) {
            super(itemView);
            txtMaDH = binding.txtMaDH;
            txtNgayDat = binding.txtNgayDat;
            txtTong = binding.txtTong;
            //  txtTenKH=binding.txtTenKH;
            txtTrangThai = binding.txtTrangThai;
            //   btnXacNhan=binding.btnXacNhan;
            //    btnDelete=binding.btnDelete;
            txtNgayXacNhan = binding.txtNgayXacNhan;
            //   txtReason=binding.txtReason;
            btnViewDetail = binding.btnViewDetail;

        }
    }
}
