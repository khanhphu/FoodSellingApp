package com.example.foodsellingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdapterDonHang;
import com.example.foodsellingapp.Model.CTDonHang;
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
import java.util.List;


public class User_OrderFragment extends Fragment {
    private RecyclerView listOrder;
    private TextView txtTongDH, txtTenKHDH, txtTrangThai;
    Button btnSaveDH, btnDeleteDH;
    private ArrayList<CTDonHang> arrDH;
    private User_AdapterDonHang adapterDonHang;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String maDH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__order, container, false);
        listOrder = view.findViewById(R.id.listOrder);
        txtTenKHDH = view.findViewById(R.id.txtTenKHDH);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        txtTongDH = view.findViewById(R.id.txtTongDH);
        btnSaveDH = view.findViewById(R.id.btnSaveDH);
        btnDeleteDH = view.findViewById(R.id.btnDeleteDH);

        //get intent
        maDH = getActivity().getIntent().getStringExtra("maDH");
        Toast.makeText(getActivity(), maDH, Toast.LENGTH_SHORT).show();
        loadDH();
        return view;

    }

    private void loadDH() {
        //init arraylist
        arrDH = new ArrayList<>();
        //

        // Get the document reference
        CollectionReference collectionReference = firestore.collection("DonHang");
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrDH.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            CTDonHang model = documentSnapshot.toObject(CTDonHang.class);
                            arrDH.add(model);
                        }
                        DocumentReference documentReference = collectionReference.document(maDH);
                        CollectionReference subReference = documentReference.collection("CT" + maDH);
                        subReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot documentSnapshot : list) {
                                    CTDonHang model = documentSnapshot.toObject(CTDonHang.class);
                                    arrDH.add(model);
                                }
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        adapterDonHang = new User_AdapterDonHang(getActivity(), arrDH);
                        //set adapter for recycler view
                        listOrder.setAdapter(adapterDonHang);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        adapterDonHang.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Show data!", Toast.LENGTH_LONG).show();
                    }


                });
    }
}


////set adapter
//                adAdapterMonAn = new Ad_AdapterMonAn(Ad_DSachMon.this,arrMonAn);
//                        //set adapter for recycler view
//                        listMonAn.setAdapter(adAdapterMonAn);
//final LinearLayoutManager layoutManager = new LinearLayoutManager(Ad_DSachMon.this, LinearLayoutManager.VERTICAL, false);
//        listMonAn.setLayoutManager(layoutManager);
//
//        adAdapterMonAn.notifyDataSetChanged();
//        Toast.makeText(Ad_DSachMon.this, "Show data!", Toast.LENGTH_LONG).show();