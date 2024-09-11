package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodsellingapp.databinding.ActivityTestPhoneAuthBinding;
import com.example.foodsellingapp.databinding.ActivityTestPhoneRegisterBinding;

public class TestPhoneRegister extends AppCompatActivity {
private ActivityTestPhoneRegisterBinding binding;
String SDT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTestPhoneRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.countryCodePicker.registerCarrierNumberEditText(binding.txtSDT);
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
    }

    private void checkData() {
        if(!binding.countryCodePicker.isValidFullNumber()){
            binding.txtSDT.setError("SDT khong hop le!");
           return;
        }
        Intent intent=new Intent(TestPhoneRegister.this, TestPhoneAuth.class);
        intent.putExtra("SDT",binding.countryCodePicker.getFullNumberWithPlus());
        startActivity(intent);
    }
}