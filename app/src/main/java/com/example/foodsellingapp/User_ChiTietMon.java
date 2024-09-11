package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.databinding.ActivityUserChiTietMonBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.integrity.p;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class User_ChiTietMon extends AppCompatActivity {
    //ko can adapter moi tu user_adapterMonan intent gui qua se set textview cho cac thuoc tinh cua file User_ChiTietMon.xml
    private ActivityUserChiTietMonBinding binding;
    String maMon;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    int num=1;
    private ManagementCart managementCart;
  private  MonAn object;
  private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserChiTietMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnBack=findViewById(R.id.btnbacktoolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( User_ChiTietMon.this,User_Menu.class));
            }
        });
    getIntentExtra();
    setVariable();
//    getDatafromIntent();
//        Toast.makeText(User_ChiTietMon.this, this.giaBan.toString(), Toast.LENGTH_SHORT).show();

   }


    private void setUpSL() {


        binding.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=num+1;
                binding.detailSLMua.setText(String.valueOf(num));
                binding.total.setText(String.valueOf(num*giaBan));
            }
        });
        binding.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(  num>1){
                    num=num-1;
                    binding.detailSLMua.setText(String.valueOf(num));
                    binding.total.setText(String.valueOf(num*giaBan));
                }

            }

    });
    }

    private void getIntentExtra() {
      object=(MonAn) getIntent().getSerializableExtra("object");
     //   maMon=getIntent().getStringExtra("maMon");

    }

    private void setVariable(){
        managementCart=new ManagementCart(User_ChiTietMon.this);
        Picasso.get().load(object.getUrl()).into(binding.detailHinhMon);
        binding.detailTenMon.setText(object.getTenMon());
        Integer giaTien=object.getGia()+object.getPhuThu();
        binding.detailGia.setText(String.valueOf(giaTien));
        binding.total.setText(String.valueOf(num*giaTien));
        binding.detailGioiThieu.setText(object.getGioiThieu());
        //them sl
        binding.btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=num+1;
                binding.detailSLMua.setText(String.valueOf(num));
                binding.total.setText(String.valueOf(num*giaTien));
            }
        });
        //giam
        binding.btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(  num>1){
                 num=num-1;
                 binding.detailSLMua.setText(String.valueOf(num));
                 binding.total.setText(String.valueOf(num*giaTien));
             }

            }
        });
        //add to cart
        binding.btnaddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberinCart(num);
                managementCart.insertFood(object);

            }
        });
        }
        String tenMon;
    Long giaBan;
    private void getDatafromIntent() {
        Long gia,phuThu;
        firebaseFirestore.collection("MonAn").document(maMon).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                      Picasso.get().load(documentSnapshot.getString("url")).into(binding.detailHinhMon);
                        binding.detailTenMon.setText(documentSnapshot.getString("tenMon"));
                        tenMon=documentSnapshot.getString("tenMon");
                         Long gia = documentSnapshot.getLong("gia");
                         Long phuThu = documentSnapshot.getLong("phuThu");
                                 giaBan=gia+phuThu;
                        binding.detailGia.setText(String.valueOf(gia.intValue()));



                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        //get imagePicasso.get().load(monAn.getUrl()).into(binding.detailHinhMon);
//        binding.detailTenMon.setText(monAn.getTenMon());
//        Integer giaBan=monAn.getGia()+monAn.getPhuThu();
//        binding.detailGia.setText(giaBan.toString());
    }


}