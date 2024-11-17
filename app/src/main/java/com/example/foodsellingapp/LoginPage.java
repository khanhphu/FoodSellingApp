package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityLoginPageBinding;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firestore.v1.FirestoreGrpc;

public class LoginPage extends AppCompatActivity {
private ActivityLoginPageBinding binding;
private FirebaseAuth fbAuth;
private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fbAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait for minutes...");
        progressDialog.setCanceledOnTouchOutside(false);
        //click cancel
        binding.btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginPage.this,RegisterPage.class));
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        //quen mk
        binding.txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, ForgotPassActivity.class));
            }
        });

    }
String email="",pass="";
    private void validateData() {
            email=binding.edtLoginEmail.getText().toString().trim();
            pass=binding.edtLoginPass.getText().toString().trim();
         if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email. Enter again",Toast.LENGTH_SHORT).show();

        }
        else if( pass.isEmpty() ){
            Toast.makeText(this,"Enter your password",Toast.LENGTH_SHORT).show();

        }
        else {
            Login();
         }
    }

    private void Login() {
        progressDialog.setMessage("Logging In___");
        progressDialog.show();
        fbAuth.signInWithEmailAndPassword(email,pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginPage.this,"Incorrect information",Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser fbUser=fbAuth.getCurrentUser();

        //retrieve firestore
        if(fbUser !=null){
            String uid= fbUser.getUid();
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            DocumentReference ref=db.collection("Users").document(uid);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                       // dua vao uid de lay ra thong tin user r dua vao usertype de pq admin hay userbth
                        String userType=doc.getString("userType");
                        if(userType.equals("user")){
                            startActivity(new Intent(LoginPage.this,User_MainPage.class));
                            finish();
                        }
                        else if(userType.equals("admin")){
                            startActivity(new Intent(LoginPage.this,Ad_MainPage.class));
                            finish();
                        }
                    }
                }
            })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(LoginPage.this,"Incorrect information",Toast.LENGTH_LONG).show();
                        }
                    });
            }
        else {
            Toast.makeText(this,"User not exist",Toast.LENGTH_SHORT).show();
        }

    }
}