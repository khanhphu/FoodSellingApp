package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityRegisterPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {
private  ActivityRegisterPageBinding binding;
private FirebaseAuth fbAuth;
private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegisterPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init fbAuth
        fbAuth=FirebaseAuth.getInstance();
        //setup progressdialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait for minutes...");
        progressDialog.setCanceledOnTouchOutside(false);
        //click cancel
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //click rgt ...
        binding.rgtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    String name="", email="",pass="", repass="";
            private void validateData() {
                name=binding.edtName.getText().toString().trim();
                email=binding.edtEmail.getText().toString().trim();
                pass=binding.edtPass.getText().toString().trim();
                repass=binding.edtRePass.getText().toString().trim();
                if(name.isEmpty() ){
                    Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show();

                }
                 else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this,"Invalid email. Enter again",Toast.LENGTH_SHORT).show();

                    }
               else if( pass.isEmpty() ){
                    Toast.makeText(this,"Enter your password",Toast.LENGTH_SHORT).show();

                }
              else  if(repass.isEmpty()){
                Toast.makeText(this,"Enter password again!",Toast.LENGTH_SHORT).show();
                }
                else {
                    createAcc();
                }
            }

    private void createAcc() {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();
        //create user auth fb
        fbAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //create account
                progressDialog.dismiss();
                updateUserInfor();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        //fail
                        progressDialog.dismiss();
                        Toast.makeText(RegisterPage.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUserInfor() {
                //*** Use firestore
        progressDialog.setMessage("Saving infor....");
        long timestamp=System.currentTimeMillis();
        String uid=fbAuth.getUid();

    }

}