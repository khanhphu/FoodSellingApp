package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PaymentConfirmation extends AppCompatActivity {
private TextView txtResult;
private int trangThaiThanhToan;
private  String maDH;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
//layout
private ImageView statusIcon;
    private TextView titleText, messageText;
    private Button btnContinue, shareButton;
    private boolean isPaymentSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);
        //initial
        statusIcon = findViewById(R.id.status_icon);
        titleText = findViewById(R.id.title_text);
        messageText = findViewById(R.id.message_text);
        btnContinue = findViewById(R.id.btnContinue);
        //shareButton = findViewById(R.id.share_button);
       // //get intent from User_DonHang
        Intent intent=getIntent();
        maDH=intent.getStringExtra("maDH");
        txtResult=findViewById(R.id.txtResult);
        txtResult.setText(intent.getStringExtra("result"));
        trangThaiThanhToan= Integer.parseInt(intent.getStringExtra("trangThaiThanhToan"));
        //handel layout - success payment or failure
        if(trangThaiThanhToan==1){
            // Success state
            statusIcon.setImageResource(R.drawable.ic_check);
            titleText.setText("Order Confirmed");
            messageText.setText("Thank you for your order. You will receive email confirmation shortly. Check the status of your order on the Order List page.");
            shareButton.setText("Share");
        }
        else{
            // Failure state
            statusIcon.setImageResource(R.drawable.ic_close);
            titleText.setText("Payment Failed");
            messageText.setText("Sorry, your payment could not be processed. Please try again or contact support.");
            shareButton.setText("Retry");
        }
        uploadDH(trangThaiThanhToan);

        btnContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, User_Home.class));
        });
    }

    private void uploadDH(int trangThaiThanhToan) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("trangThaiThanhToan",1); //thanh toan thanh cong
        DocumentReference documentReference=firestore.collection("DonHang")
                .document(maDH);
        documentReference.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PaymentConfirmation.this,
                                "Upload payment status from ZaloPay for  "+maDH+"successfully!",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentConfirmation.this,
                                e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}