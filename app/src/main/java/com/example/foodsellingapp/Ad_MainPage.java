package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodsellingapp.databinding.ActivityAdMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Ad_MainPage extends AppCompatActivity {
private ActivityAdMainPageBinding binding;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkUsers();
        binding.secLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                //nhay vao cau lenh if :
                checkUsers();
            }
        });
    }
    private void checkUsers(){
        FirebaseUser fbU=firebaseAuth.getInstance().getCurrentUser();
        if(fbU==null){
            //not log in -> main
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        else{
            binding.txtAdEmail.setText((String)fbU.getEmail());
        }
    }
}