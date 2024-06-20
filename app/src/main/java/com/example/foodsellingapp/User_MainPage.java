package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.example.foodsellingapp.databinding.ActivityUserMainPageBinding;
import com.google.android.material.navigation.NavigationView;

public class User_MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
private ActivityUserMainPageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //xu ly
        setSupportActionBar(binding.toolbar);
        binding.navmenu.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open_nav,R.string.close_nav);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//Fragment:
        if(item.getItemId()==R.id.it_home){
            startActivity(new Intent(this, User_MainPage.class));
        }
        else if(item.getItemId()==R.id.it_cart){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new User_ShoppingCartFragment()).commit();

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
       if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
           binding.drawerLayout.closeDrawer(GravityCompat.START);
       }
       else{
           super.onBackPressed();
       }
    }
}