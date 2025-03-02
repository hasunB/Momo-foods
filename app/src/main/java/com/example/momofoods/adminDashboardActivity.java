package com.example.momofoods;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.momofoods.adminfragments.homeFragment;
import com.example.momofoods.adminfragments.orderFragment;
import com.example.momofoods.adminfragments.productFragment;
import com.example.momofoods.adminfragments.userFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class adminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavView = findViewById(R.id.adminbottomNavView);
        bottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeFragment2) {
                replaceFragment(new homeFragment());
            }

            if (item.getItemId() == R.id.orderFragment) {
                replaceFragment(new orderFragment());
            }

            if (item.getItemId() == R.id.productFragment) {
                replaceFragment(new productFragment());
            }

            if (item.getItemId() == R.id.userFragment) {
                replaceFragment(new userFragment());
            }
            return true;
        });


    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.adminfragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }
}