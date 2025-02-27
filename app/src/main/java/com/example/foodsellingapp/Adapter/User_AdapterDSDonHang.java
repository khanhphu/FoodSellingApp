package com.example.foodsellingapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.R;
import com.example.foodsellingapp.databinding.ListUserDsachdonhangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                holder.btnXacNhan.setVisibility(View.INVISIBLE);
                holder.btnDelete.setVisibility(View.INVISIBLE);
                holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                holder.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
                break;
            case "choXacNhan":
                holder.txtTrangThai.setText("Chờ xác nhận");
                holder.btnXacNhan.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                break;
            case "Huy":
                holder.txtTrangThai.setText("Hủy");
                holder.btnXacNhan.setVisibility(View.INVISIBLE);
                holder.btnDelete.setVisibility(View.INVISIBLE);
                holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                holder.txtNgayXacNhan.setText(donHang.getNgayXacNhan());
                break;
        }

        holder.txtTong.setText("" + donHang.getTongCong());
        //load tenKH from maKH
        holder.txtMaDH.setText(donHang.getMaDH());
        holder.txtNgayDat.setText(donHang.getNgayTao());
        loadMaKH(donHang, holder);
        loaduT(donHang);
        // Hide buttons if the logged-in user is the order owner
        if (firebaseUser.getUid().equals(donHang.getMaKH())) {
            holder.btnXacNhan.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        //xac nhan don
        holder.btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferDateTime();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("trangThai", "XacNhan");
                hashMap.put("ngayXacNhan", ngayXacNhanDon);
                DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
                docRef.update(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.txtTrangThai.setText("Xác nhận");
                                holder.btnXacNhan.setVisibility(View.INVISIBLE);
                                holder.btnDelete.setVisibility(View.INVISIBLE);
                                //cap nhat ngay xac nhan don
                                holder.txtNgayXacNhan.setText(ngayXacNhanDon);
                                holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "Xác nhận đơn!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.logofood_ic);
                builder.setTitle("Cancel Order");
                builder.setMessage("Do you want cancel this order?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDeleteOrder(holder, donHang);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    public void updateDeleteOrder(HolderDSDH holder, DonHang donHang) {
        //ngay huy
        transferDateTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("trangThai", "Huy");
        hashMap.put("ngayXacNhan", ngayXacNhanDon);
        DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
        docRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        holder.btnXacNhan.setVisibility(View.INVISIBLE);
                        holder.btnDelete.setVisibility(View.INVISIBLE);
                        holder.txtNgayXacNhan.setVisibility(View.VISIBLE);
                        holder.txtNgayXacNhan.setText(ngayXacNhanDon);
                        Toast.makeText(context, "Hủy đơn!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void transferDateTime() {
        //xu ly ngay gio
        long timestampt = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestampt);
        ngayXacNhanDon = (String) DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar);
    }

    private void loadMaKH(DonHang donHang, HolderDSDH holder) {

        DocumentReference documentReference = firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        holder.txtTenKH.setText(task.getResult().getString("name"));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderDSDH extends RecyclerView.ViewHolder {
        TextView txtMaDH, txtTenKH, txtNgayDat, txtTrangThai, txtTong, txtNgayXacNhan;
        ImageView btnXacNhan, btnDelete;

        public HolderDSDH(@NonNull View itemView) {
            super(itemView);
            txtMaDH = binding.txtMaDH;
            txtNgayDat = binding.txtNgayDat;
            txtTong = binding.txtTong;
            txtTenKH = binding.txtTenKH;
            txtTrangThai = binding.txtTrangThai;
            btnXacNhan = binding.btnXacNhan;
            btnDelete = binding.btnDelete;
            txtNgayXacNhan = binding.txtNgayXacNhan;

        }
    }
}
