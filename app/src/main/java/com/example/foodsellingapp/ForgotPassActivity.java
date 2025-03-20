package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityForgotPassBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private ActivityForgotPassBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button forgotSubmit;
    private EditText email;
    private TextView mess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        //init Firebase
        firebaseAuth=FirebaseAuth.getInstance();
        //init progressDialog
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
//        binding.btnForgotMail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  validateForgot();
//                Toast.makeText(ForgotPassActivity.this,"Click",Toast.LENGTH_SHORT).show();
//            }
//        });
        forgotSubmit=findViewById(R.id.btnForgotMail);
        forgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForgot();
            }
        });
        email=findViewById(R.id.txtForgotMail);
        //
//        mess=findViewById(R.id.btnMess);
//        mess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(ForgotPassActivity.this,PushMessaging.class);
//                startActivity(intent);
//            }
//        });
    }
    private String for_email="";
    private void validateForgot() {
        for_email=email.getText().toString().trim();
        if(for_email.isEmpty()){
            Toast.makeText(this,"Not empty!", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(for_email).matches()){
            Toast.makeText(this,"Invalid email format", Toast.LENGTH_SHORT).show();

        }
        else {
            recoverPass();
        }

    }

    private void recoverPass() {
        progressDialog.setMessage("Seding password recovery instruction to "+for_email);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(for_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPassActivity.this,"Instructions to reset password sent to"+for_email, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassActivity.this,"Failed to sent due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}