package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityUserPro5Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User_Pro5 extends AppCompatActivity {
ImageView btnback;
private ActivityUserPro5Binding binding;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserPro5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //back
        btnback=findViewById(R.id.btnbacktoolbar);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //user pro5
        firebaseAuth=FirebaseAuth.getInstance();

        loadPro5();

    }

    private void loadPro5() {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        DocumentReference ref=firestore.collection("Users").document(user.getUid());
           ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   String name=documentSnapshot.getString("name");
                   binding.txtUserName.setText(name);
                   binding.txtuserMail.setText(documentSnapshot.getString("email"));
               }
           });

    }
}