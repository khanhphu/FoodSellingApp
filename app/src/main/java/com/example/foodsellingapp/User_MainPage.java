package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.databinding.ActivityUserMainPageBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class User_MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityUserMainPageBinding binding;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    String uid=firebaseAuth.getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         checkUsers();

       // bottom menu:
        replaceFragment(new User_Home());

        binding.bottomAppBar.setBackground(null);
        binding.bottomMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              if(item.getItemId()==R.id.btnShopCart){
                  replaceFragment(new User_ShoppingCartFragment());
              }
              else if(item.getItemId()==R.id.btnOrder){
                  startActivity(new Intent(User_MainPage.this,User_DonHang.class));
              }
              else  if(item.getItemId()==R.id.btnhome){
                  try {
                      replaceFragment(new User_Home());
                  }
                  catch (Exception ex){
                      Toast.makeText(User_MainPage.this, "Cannot render to UserMainPage!", Toast.LENGTH_LONG).show();

                  }
                }
              else  if(item.getItemId()==R.id.btnMenu){
                  try {
                      startActivity(new Intent(User_MainPage.this,User_Menu.class));
                  }
                  catch (Exception ex){
                      Toast.makeText(User_MainPage.this, "Cannot render to UserMainPage!", Toast.LENGTH_LONG).show();

                  }
                }
              return true;
            }
        });
        //navigation menu:
        setSupportActionBar(binding.toolbar);
        binding.navmenu.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //bottom menu:
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    private void checkUsers() {
        FirebaseUser fbU = firebaseAuth.getCurrentUser();
        if (fbU == null) {
            //not log in -> main
            startActivity(new Intent(this, LoginPage.class));
            finish();
            Toast.makeText(User_MainPage.this, "Logout account successfully!", Toast.LENGTH_LONG).show();

        } else {
            String emailUser = fbU.getEmail();
            Toast.makeText(User_MainPage.this, firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.it_home) {
            replaceFragment(new User_Home());
        }
        //Fragment:
        else if (id== R.id.it_cart) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new User_ShoppingCartFragment()).commit();


            //Activity:
        } else if (id== R.id.it_pro5) {
            try {
                startActivity(new Intent(User_MainPage.this, User_Pro5.class));
            }
            catch (Exception ex){
                Toast.makeText(User_MainPage.this, "Cannot render to UserProfilePage!", Toast.LENGTH_LONG).show();

            }
        } else if (id == R.id.it_logout) {
            Toast.makeText(User_MainPage.this, "Logout account!", Toast.LENGTH_LONG).show();
           // fix bug:22/6/24
            FirebaseAuth.getInstance().signOut();
            //nhay vao cau lenh if :
            checkUsers();
       }
        else if (id == R.id.it_order) {
            try {
                startActivity(new Intent(User_MainPage.this, User_DonHang.class));

            }
            catch (Exception ex){
                Toast.makeText(User_MainPage.this, "Cannot render to User_DonHang!", Toast.LENGTH_LONG).show();

            }
       }
        else if (id == R.id.it_menu) {
            try {
                startActivity(new Intent(User_MainPage.this, User_Menu.class));

            }
            catch (Exception ex){
                Toast.makeText(User_MainPage.this, "Cannot render to User_Menu!", Toast.LENGTH_LONG).show();

            }
       }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}