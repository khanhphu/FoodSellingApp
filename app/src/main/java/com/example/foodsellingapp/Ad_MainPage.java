package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodsellingapp.databinding.ActivityAdMainPageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Ad_MainPage extends AppCompatActivity {
    private ActivityAdMainPageBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkUsers();
        binding.secLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //nhay vao cau lenh if :
                checkUsers();
            }
        });
        //them mon
        binding.secAddMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this,Ad_ThemMon.class));
            }
        });
        binding.secDSachMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this,Ad_DSachMon.class));
            }
        });
        binding.secCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this,Ad_Categories.class));

            }
        });
        binding.secDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this, Ad_DSDonHang.class));

            }
        });
        binding.secKhachHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this, Ad_DSKhachHang.class));
            }
        });
        binding.secThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ad_MainPage.this, Ad_ThongKe.class));
            }
        });

    }
    private void checkUsers(){
        FirebaseUser fbU=firebaseAuth.getInstance().getCurrentUser();
        if(fbU==null){
            //not log in -> main
            startActivity(new Intent(this,MainActivity.class));
            finish();
            //onBackPressed();
        }
        else{
            //ko null gui uid de set text nam and email for admin
            String uid=fbU.getUid();
            // retrieve data with special condition
            loadAdminPro5(uid);
        }
    }

    private void loadAdminPro5(String uid) {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot qs:queryDocumentSnapshots ){
                            if(qs.getString("uid").contains(uid)&& qs.getString("userType").contains("admin")){
                                binding.txtAdName.setText(qs.getString("name"));
                                binding.txtAdEmail.setText(qs.getString("email"));
                            }
                        }
                    }
                });

    }
}