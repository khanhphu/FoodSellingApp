package com.example.foodsellingapp;

import com.example.foodsellingapp.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodsellingapp.User_Home;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.foodsellingapp.R.layout.activity_splash_screen);

        // Find views
        ImageView splashLogo = findViewById(R.id.splash_logo);
        TextView splashAppName = findViewById(R.id.splash_app_name);
        TextView splashTagline = findViewById(R.id.splash_tagline);

        // Animate logo (scale animation)
        splashLogo.setScaleX(0f);
        splashLogo.setScaleY(0f);
        splashLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .start();

        // Animate app name (fade in)
        splashAppName.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(300)
                .start();

        // Animate tagline (fade in)
        splashTagline.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .start();

        // Navigate to the main activity after a delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, Onboarding.class);
            startActivity(intent);
            finish();
        }, 2500); // 2.5 seconds delay
    }
}