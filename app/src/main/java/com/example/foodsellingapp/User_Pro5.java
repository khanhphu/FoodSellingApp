package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import javax.crypto.spec.PSource;

public class User_Pro5 extends AppCompatActivity {
    ImageView btnback;
    private ActivityUserPro5Binding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private static final String TAG = "PROFILE_SETTING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPro5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //user pro5
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadPro5();
        binding.btnSetting.setOnClickListener(v -> {
            try{
                startActivity(new Intent(this, User_SettingAccount.class));

            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        });
        countOrders();
    }

    private void countOrders() {
        firestore.collection("DonHang").whereEqualTo("maKH", firebaseAuth.getCurrentUser().getUid())

                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        long orderCount = value.size();
                        binding.countOrder.setText(String.valueOf(orderCount));
                    }
                });
    }


    private void loadPro5() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DocumentReference ref = firestore.collection("Users").document(user.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                binding.txtUserName.setText(name);
                binding.txtEmail.setText(documentSnapshot.getString("email"));
                String imageUrl = documentSnapshot.getString("profileImageUrl");
                if (imageUrl == null) {
                    Picasso.get().load(R.drawable.user_ic).into(binding.imgUser);
                }
                else{
                    Picasso.get().load(imageUrl).into(binding.imgUser);

                }



            }
        });

    }
}