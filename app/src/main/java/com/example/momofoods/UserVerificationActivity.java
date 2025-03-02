package com.example.momofoods;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent  = getIntent();
        int vcode = intent.getIntExtra("Verification",0);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Button verifyUser = findViewById(R.id.button8);
        verifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText vcodeText = findViewById(R.id.editTextText9);
                int enteredVcode = Integer.parseInt(vcodeText.getText().toString());

                if(enteredVcode == vcode){
                    Toast.makeText(UserVerificationActivity.this,"Verification Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserVerificationActivity.this, SignInActivity.class);
                    startActivity(intent);
                    editor.putString("isLoggedIn", "true");
                    finish();
                } else {
                    vcodeText.setError("Invalid Verification Code. Try Again");
                    editor.putString("isLoggedIn", "false");
                }
            }
        });

        editor.apply();

    }
}