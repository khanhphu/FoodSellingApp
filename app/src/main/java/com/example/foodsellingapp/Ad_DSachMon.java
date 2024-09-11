package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.Ad_AdapterMonAn;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.databinding.ActivityAdDsachMonBinding;
import com.example.foodsellingapp.databinding.ActivityAdThemMonBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Ad_DSachMon extends AppCompatActivity {
private ActivityAdDsachMonBinding binding;
private ArrayList<MonAn> arrMonAn;
private Ad_AdapterMonAn adAdapterMonAn;
FirebaseFirestore firestore;
RecyclerView listMonAn;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdDsachMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listMonAn=binding.listAdMon;
        //refresh when crud item in list mon
        loadMon();
        binding.swipetorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMon();
                binding.swipetorefresh.setRefreshing(false);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        binding.secSearch.setText(user.getEmail());
    }

    private void loadMon() {
        //init arraylist
        arrMonAn = new ArrayList<>();
        //
        firestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firestore.collection("MonAn");
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrMonAn.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            MonAn model = documentSnapshot.toObject(MonAn.class);
                            arrMonAn.add(model);
                        }
                        //set adapter
                        adAdapterMonAn = new Ad_AdapterMonAn(Ad_DSachMon.this,arrMonAn);
                        //set adapter for recycler view
                        listMonAn.setAdapter(adAdapterMonAn);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(Ad_DSachMon.this, LinearLayoutManager.VERTICAL, false);
                        listMonAn.setLayoutManager(layoutManager);
                        adAdapterMonAn.notifyDataSetChanged();
                        Toast.makeText(Ad_DSachMon.this, "Show data!", Toast.LENGTH_LONG).show();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ad_DSachMon.this, "Cannot show data!", Toast.LENGTH_LONG).show();
                    }
                });

    }
}