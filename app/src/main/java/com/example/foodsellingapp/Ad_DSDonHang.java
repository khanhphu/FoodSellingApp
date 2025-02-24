package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterDSDonHang;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.databinding.ActivityDsdonHangBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Ad_DSDonHang extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityDsdonHangBinding binding;
    private ArrayList<DonHang> arrayList;
    private User_AdapterDSDonHang adapterDSDonHang;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    RecyclerView listDH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsdonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDSDH();
        binding.swipetorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDSDH();
                binding.swipetorefresh.setRefreshing(false);
            }
        });
        //spinner
        String[] statusOrder = getResources().getStringArray(R.array.statusorder);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOrder);
        binding.spinnerOrder.setAdapter(adapter);
        binding.spinnerOrder.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedStatus = parent.getItemAtPosition(position).toString();
        filterItems(selectedStatus);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void filterItems(String selectedStatus) {
        switch (selectedStatus) {
            case "Chờ xác nhận":
                loadDSDHFilter("choXacNhan");
                break;

            case "Xác nhận":
                loadDSDHFilter("XacNhan");

                break;

            case "Hủy":
                loadDSDHFilter("Huy");

                break;

            default:
                loadDSDH();
                break;
        }

    }

    private void loadDSDHFilter(String status) {
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DonHang")
                .whereEqualTo("trangThai", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DonHang donHang = document.toObject(DonHang.class);
                            arrayList.add(donHang);
                        }
                        adapterDSDonHang.notifyDataSetChanged();
                        Toast.makeText(Ad_DSDonHang.this, "Show filter", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("FetchStatusOrder", "Error fetching status", task.getException());
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
                        adapterDSDonHang = new User_AdapterDSDonHang(Ad_DSDonHang.this, arrayList);
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


