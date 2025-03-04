package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.User_AdpaterMonAn;
import com.example.foodsellingapp.Model.MonAn;
import com.example.foodsellingapp.databinding.ActivityUserMenuBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class User_Menu extends AppCompatActivity {
private ActivityUserMenuBinding binding;
private ArrayList<MonAn> arrMon;
private User_AdpaterMonAn userAdpaterMonAn;
   ImageView btnBack;
   TextView secSearch;
FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        btnBack=findViewById(R.id.btnbacktoolbar);
        secSearch=findViewById(R.id.secSearch);

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        loadMon();
        secSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    userAdpaterMonAn.getFilter().filter(s);
                }
                catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadMon() {
        arrMon = new ArrayList<>();
        //
        firestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firestore.collection("MonAn");
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrMon.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            MonAn model = documentSnapshot.toObject(MonAn.class);
                            arrMon.add(model);
                        }
                        //set adapter
                        userAdpaterMonAn = new User_AdpaterMonAn(User_Menu.this,arrMon);
                        //set adapter for recycler view
                        binding.menu4User.setAdapter(userAdpaterMonAn);
                        userAdpaterMonAn.notifyDataSetChanged();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(User_Menu.this, "Cannot show data!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}