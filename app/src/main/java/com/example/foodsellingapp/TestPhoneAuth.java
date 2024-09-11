package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityTestPhoneAuthBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class TestPhoneAuth extends AppCompatActivity {
private ActivityTestPhoneAuthBinding binding;
FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
String SDT, verifyOTP;
PhoneAuthProvider.ForceResendingToken resendingToken;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTestPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Verify...");
    //    progressDialog.show();
        SDT=getIntent().getStringExtra("SDT");
        Toast.makeText(this,SDT,Toast.LENGTH_SHORT).show();
        sendOTP();
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.txtOTP.getText().toString().isEmpty()){
                    Toast.makeText(TestPhoneAuth.this,"OTP not match",Toast.LENGTH_LONG).show();
                }
                else{
                    progressDialog.setMessage("signInWithPhoneAuthCredential(credential)");
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyOTP,binding.txtOTP.getText().toString().trim());
                        signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void sendOTP() {
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(SDT)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d("onVerificationCompleted", "onVerificationCompleted:" + phoneAuthCredential);

                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(TestPhoneAuth.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            Log.e("onVerificationFailed",e.getMessage().toString());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verifyOTP=s;
                        resendingToken=forceResendingToken;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      progressDialog.dismiss();
                        FirebaseUser user = task.getResult().getUser();
                      startActivity(new Intent(TestPhoneAuth.this,MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }



}