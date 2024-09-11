package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterDonHang;
import com.example.foodsellingapp.Model.CTDonHang;
import com.example.foodsellingapp.databinding.ActivityUserDonHangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User_DonHang extends AppCompatActivity {
    private ActivityUserDonHangBinding binding;
    String maDH;
    private ArrayList<CTDonHang> arrDH;
    private User_AdapterDonHang adapterDonHang;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get intent


            String maDon=getIntent().getStringExtra("maDH");
            if(maDon==null){
                binding.secEmpty.setVisibility(View.VISIBLE);
                binding.secOrder.setVisibility(View.GONE);
            }
            else {
                binding.secOrder.setVisibility(View.VISIBLE);
                binding.secEmpty.setVisibility(View.GONE);
                binding.btnHome.setVisibility(View.GONE);
                this.maDH = getIntent().getStringExtra("maDH");
                Toast.makeText(User_DonHang.this, maDH, Toast.LENGTH_SHORT).show();
                //load in4 :
                loadCTDH();
                updateDH();
                handleRadioButton();
            }



        binding.btnDeleteDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDH();
            }
        });
        binding.btnSaveDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDH();
            }
        });
        //
        binding.btnDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnDH.setBackgroundResource(R.drawable.border4userpro5);
                binding.btnMoreDH.setBackgroundResource(R.drawable.checkout_frame);
            }
        });
        binding.btnMoreDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnMoreDH.setBackgroundResource(R.drawable.border4userpro5);
                binding.btnDH.setBackgroundResource(R.drawable.checkout_frame);
                 startActivity(new Intent(User_DonHang.this,User_DSDonHang.class));
            }
        });
        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User_DonHang.this,User_Menu.class));

            }
        });
        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(  User_DonHang.this, User_MainPage.class));
            }
        });

    }

    private void deleteDH() {
        CollectionReference collectionRef = firestore.collection("DonHang");
        DocumentReference docRef = collectionRef.document(maDH);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log.d("CKTAG", "Mon an da xoa !");
                        Toast.makeText(User_DonHang.this,"Xóa đơn hàng mã"+maDH+" thành công!",Toast.LENGTH_SHORT).show();
                        binding.secOrder.setVisibility(View.GONE);
                        binding.secEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CKTAG", "Error deleting document", e);
                        Toast.makeText(User_DonHang.this,"Lỗi khi xóa đơn !",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void saveDH() {
       if(binding.txtSDT.getText().toString().isEmpty()){
           binding.txtSDT.setError("Enter your phone number");
           return;
       }
       if(binding.rdtGiao.isChecked()==false && binding.rdtLay.isChecked()==false){
           Toast.makeText(User_DonHang.this,"Select delivery !",Toast.LENGTH_SHORT).show();
           return;
       }
       if(binding.rdtTM.isChecked()==false && binding.rdtCK.isChecked()==false){
           Toast.makeText(User_DonHang.this,"Select payment !",Toast.LENGTH_SHORT).show();
           return;
       }
       String sdt=binding.txtSDT.getText().toString();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("lienHe",sdt);
        hashMap.put("ptThanhToan",pay);
        hashMap.put("ptNhanHang",giaoHang);
        DocumentReference docRef = firestore.collection("DonHang").document(maDH);
        docRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(User_DonHang.this, "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                        binding.secOrder.setVisibility(View.GONE);
                        binding.secEmpty.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(User_DonHang.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
String giaoHang;
    String pay;
    private void handleRadioButton() {
        //delivery
        binding.grRdt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==binding.rdtLay.getId()){
                    giaoHang="Đến lấy";

                }
                else if(checkedId==binding.rdtGiao.getId()){
                    giaoHang="Giao hàng";
                }
            }
        });
        //payment
        binding.grRdtPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==binding.rdtTM.getId()){
                    pay="Tiền mặt";

                }
                else if(checkedId==binding.rdtCK.getId()){
                    pay="Chuyển khoản";
                }
            }
        });

    }

    private void updateDH() {
        CollectionReference collectionReference=firestore.collection("DonHang");
        DocumentReference documentReference=collectionReference.document(maDH);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int tong=task.getResult().getDouble("tongCong").intValue();
                binding.txtTongDH.setText(""+tong);
//                binding.txtTrangThai.setText(task.getResult().getString("trangThai"));
                binding.txtTrangThai.setText("Chờ xác nhận");
                String uid=task.getResult().getString("maKH");
                loadUser(uid);
            }
        });
    }

    private void loadUser(String uid) {
        CollectionReference collectionReference=firestore.collection("Users");
        DocumentReference documentReference=collectionReference.document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                binding.txtTenKHDH.setText(task.getResult().getString("name"));
            }
        });
    }



    private void loadCTDH() {
        //init arraylist
        arrDH = new ArrayList<>();

        // Get the document reference

          CollectionReference subReference=firestore.collection("DonHang").document(maDH).collection("CT"+maDH);


                subReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                arrDH.clear();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot documentSnapshot : list) {
                                    CTDonHang model = documentSnapshot.toObject(CTDonHang.class);
                                    arrDH.add(model);
                                }

                                adapterDonHang = new User_AdapterDonHang(User_DonHang.this, arrDH);


                                //set adapter for recycler view
                                binding.listOrder.setAdapter(adapterDonHang);
                                final LinearLayoutManager layoutManager = new LinearLayoutManager(User_DonHang.this, LinearLayoutManager.VERTICAL, false);
                                adapterDonHang.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

    }
}




