package com.example.foodsellingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.foodsellingapp.Adapter.Ad_AdapterMonAn;
import com.example.foodsellingapp.Adapter.User_AdpaterMonAn;
import com.example.foodsellingapp.Model.MonAn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class User_Home extends Fragment {
    private ViewFlipper viewFlipper;
    private ArrayList<MonAn> arrmon;
    private User_AdpaterMonAn adpaterMonAn;
    RecyclerView menuMon;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_user__home,container,false);
        viewFlipper=view.findViewById(R.id.viewFlipper);
        menuMon=view.findViewById(R.id.userListMon);
        loadMon();
        return  view;

    }
    private void loadMon() {
        //init arraylist
        arrmon = new ArrayList<>();
        //
        firestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firestore.collection("MonAn");
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrmon.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            MonAn model = documentSnapshot.toObject(MonAn.class);
                            arrmon.add(model);
                        }
                        //set adapter
                        adpaterMonAn = new User_AdpaterMonAn(getActivity(),arrmon);
                        //set adapter for recycler view
                        menuMon.setAdapter(adpaterMonAn);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        menuMon.setLayoutManager(layoutManager);

                        adpaterMonAn.notifyDataSetChanged();
                        //Toast.makeText(getActivity(), "Show data!", Toast.LENGTH_LONG).show();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Cannot show data!", Toast.LENGTH_LONG).show();
                    }
                });

    }
}