package com.example.momofoods;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.momofoods.fragments.CartFragment;
import com.example.momofoods.fragments.CouponFragment;
import com.example.momofoods.fragments.HomeFragment;
import com.example.momofoods.fragments.profileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String fragment = intent.getStringExtra("fragment");

        if (fragment != null && fragment.equals("cart")) {
            replaceFragment(new CartFragment());
        } else {
            replaceFragment(new HomeFragment());
        }

        BottomNavigationView bottomNavView = findViewById(R.id.adminbottomNavView);
        bottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeFragment) {
                replaceFragment(new HomeFragment());
            }

            if (item.getItemId() == R.id.couponFragment) {
                replaceFragment(new CouponFragment());
            }

            if (item.getItemId() == R.id.cartFragment) {
                replaceFragment(new CartFragment());
            }

            if (item.getItemId() == R.id.profileFragment) {
                replaceFragment(new profileFragment());
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