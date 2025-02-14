package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.Ad_AdapterUsers;
import com.example.foodsellingapp.Model.Users;
import com.example.foodsellingapp.databinding.ActivityAdDskhachHangBinding;
import com.example.foodsellingapp.databinding.ListAdDsachkhBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Ad_DSKhachHang extends AppCompatActivity {
private ArrayList<Users> arr_Users;
private ActivityAdDskhachHangBinding binding;
private Ad_AdapterUsers adAdapterUsers;
FirebaseFirestore firestore=FirebaseFirestore.getInstance();
RecyclerView listUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdDskhachHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDSUsers();
        binding.swipetorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDSUsers();
                binding.swipetorefresh.setRefreshing(false);
            }
        });
    }
    private void loadDSUsers(){
        arr_Users=new ArrayList<>();
        CollectionReference ref=firestore.collection("Users");
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arr_Users.clear();
                        List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot documentSnapshot:list){
                            Users model=documentSnapshot.toObject(Users.class);
                            arr_Users.add(model);
                        }
                        //set adapter
                        adAdapterUsers =new Ad_AdapterUsers(Ad_DSKhachHang.this,arr_Users);
                        //set adapter for recycler view
                        binding.listAdUsers.setAdapter(adAdapterUsers);
                        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(Ad_DSKhachHang.this,LinearLayoutManager.VERTICAL,false);
                        binding.listAdUsers.setLayoutManager(linearLayoutManager);
                        adAdapterUsers.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ad_DSKhachHang.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}