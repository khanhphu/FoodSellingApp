package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
Button btnRegister, btnLog, btnTestNavMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegister=findViewById(R.id.btnRegis);
        btnLog=findViewById(R.id.btnLog);
        btnTestNavMenu=findViewById(R.id.btnTestNavMenu);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterPage.class));
            }
        });
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginPage.class));
            }
        });
        btnTestNavMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,User_MainPage.class));
            }
        });
    }
//You have done some tasks. Congratulation dude, break a leg !


}
