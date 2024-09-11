package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.Ad_AdapterMonAn;
import com.example.foodsellingapp.Adapter.User_AdapterDSDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.databinding.ActivityUserDsdonHangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class User_DSDonHang extends AppCompatActivity {
    private ActivityUserDsdonHangBinding binding;
    private ArrayList<DonHang> arrayList;
    private User_AdapterDSDonHang adapterDSDonHang;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String maKH;
ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDsdonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get maKH
        maKH = firebaseAuth.getCurrentUser().getUid();
        loadDH();
        btnBack=findViewById(R.id.btnbacktoolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadDH() {
        arrayList=new ArrayList<>();
        CollectionReference collectionReference = firestore.collection("DonHang");
        collectionReference.whereEqualTo("maKH", this.maKH).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrayList.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            DonHang model = documentSnapshot.toObject(DonHang.class);
                            arrayList.add(model);
                        }
                        //set adapter
                        adapterDSDonHang = new User_AdapterDSDonHang(User_DSDonHang.this, arrayList);
                        //set adapter for recycler view
                        binding.userListDH.setAdapter(adapterDSDonHang);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(User_DSDonHang.this, LinearLayoutManager.VERTICAL, false);
                        binding.userListDH.setLayoutManager(layoutManager);
                        adapterDSDonHang.notifyDataSetChanged();
                        Toast.makeText(User_DSDonHang.this, "Show data!", Toast.LENGTH_LONG).show();
                    }
                });

    }
}