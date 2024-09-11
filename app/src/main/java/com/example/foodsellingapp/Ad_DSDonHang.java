package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterDSDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityDsdonHangBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Ad_DSDonHang extends AppCompatActivity {
private ActivityDsdonHangBinding binding;
    private ArrayList<DonHang> arrayList;
    private User_AdapterDSDonHang adapterDSDonHang;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    RecyclerView listDH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDsdonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDSDH();
        binding.swipetorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDSDH();
                binding.swipetorefresh.setRefreshing(false);
            }
        });
    }

    private void loadDSDH() {
        arrayList = new ArrayList<>();
        //

        CollectionReference ref = firestore.collection("DonHang");
        ref.get()
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
                        adapterDSDonHang = new User_AdapterDSDonHang(Ad_DSDonHang.this,arrayList);
                        //set adapter for recycler view
                        binding.listDH.setAdapter(adapterDSDonHang);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(Ad_DSDonHang.this, LinearLayoutManager.VERTICAL, false);
                        binding.listDH.setLayoutManager(layoutManager);
                        adapterDSDonHang.notifyDataSetChanged();
                        Toast.makeText(Ad_DSDonHang.this, "Show data!", Toast.LENGTH_LONG).show();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ad_DSDonHang.this, "Cannot show data!", Toast.LENGTH_LONG).show();
                    }
                });
    }


}