package com.example.foodsellingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsellingapp.Model.DonHang;
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
import java.util.HashMap;


//adpater nay dung cho ca admin va khahc hang
public class User_AdapterDSDonHang extends RecyclerView.Adapter<User_AdapterDSDonHang.HolderDSDH> {
    private ListUserDsachdonhangBinding binding;
    private Context context;
    private ArrayList<DonHang> arrayList;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    //constructor

    public User_AdapterDSDonHang(Context context, ArrayList<DonHang> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HolderDSDH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ListUserDsachdonhangBinding.inflate(LayoutInflater.from(context),parent,false);

        return new User_AdapterDSDonHang.HolderDSDH(binding.getRoot());
    }
    String userType;
    @Override
    public void onBindViewHolder(@NonNull HolderDSDH holder, int position) {

            DonHang donHang=arrayList.get(position);

            if(donHang.getTrangThai().equals("XacNhan")){
            holder.txtTrangThai.setText("Xác nhận");
            holder.btnXacNhan.setVisibility(View.INVISIBLE);
            holder.btnDelete.setVisibility(View.INVISIBLE);
            }

            else if(donHang.getTrangThai().equals("choXacNhan")){
                holder.txtTrangThai.setText("Chờ xác nhận");
            }
            else if(donHang.getTrangThai().equals("Huy")){
                holder.txtTrangThai.setText("Hủy");
                holder.btnXacNhan.setVisibility(View.INVISIBLE);
                holder.btnDelete.setVisibility(View.INVISIBLE);
            }

            holder.txtTong.setText(""+donHang.getTongCong());
            //load tenKH from maKH
        holder.txtMaDH.setText(donHang.getMaDH());
        holder.txtNgayDat.setText(donHang.getNgayTao());
            loadMaKH(donHang,holder);
            loaduT(donHang);
            if(firebaseUser.getUid().equals(donHang.getMaKH())){
                holder.btnXacNhan.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            }
            else{
                holder.btnXacNhan.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
            //xac nhan don
            holder.btnXacNhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("trangThai","XacNhan");
                    DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
                    docRef.update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.txtTrangThai.setText("Xác nhận");
                                    holder.btnXacNhan.setVisibility(View.GONE);
                                    holder.btnDelete.setVisibility(View.GONE);
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
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("trangThai","Huy");
                    DocumentReference docRef = firestore.collection("DonHang").document(donHang.getMaDH());
                    docRef.update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.btnXacNhan.setVisibility(View.GONE);
                                    holder.btnDelete.setVisibility(View.GONE);
                                    Toast.makeText(context, "Hủy đơn!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

    }

    private void loaduT(DonHang donHang) {

        DocumentReference documentReference=firestore.collection("Users").document(donHang.getMaKH());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              String  userType1 =documentSnapshot.getString("userType");
                         convertUserType(userType1,userType);
            }

        });

    }

    private void convertUserType(String userType1, String userType) {
        userType=userType1;
    }


    private void loadMaKH(DonHang donHang, HolderDSDH holder) {

        DocumentReference documentReference=firestore.collection("Users").document(donHang.getMaKH());
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
        TextView txtMaDH,txtTenKH,txtNgayDat,txtTrangThai,txtTong;
        ImageView btnXacNhan, btnDelete;
        public HolderDSDH(@NonNull View itemView) {
            super(itemView);
            txtMaDH=binding.txtMaDH;
            txtNgayDat=binding.txtNgayDat;
            txtTong=binding.txtTong;
            txtTenKH=binding.txtTenKH;
            txtTrangThai=binding.txtTrangThai;
            btnXacNhan=binding.btnXacNhan;
            btnDelete=binding.btnDelete;

        }
    }
}
