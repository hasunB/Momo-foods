package com.example.momofoods;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class adminSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button signBtn = findViewById(R.id.button2);
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mobile = findViewById(R.id.editTextText);
                EditText password = findViewById(R.id.editTextText2);
                String mobileText = mobile.getText().toString();
                String passwordText = password.getText().toString();

                if(mobileText.isEmpty()){
                    mobile.setError("Please enter mobile number");
                } else if(passwordText.isEmpty()){
                    password.setError("Please enter password");
                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("admin").whereEqualTo("mobile",mobileText).whereEqualTo("password",passwordText).get(Source.SERVER)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful() && task.getResult() != null){
                                        if(task.getResult().isEmpty()){
                                            Toast.makeText(adminSignInActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(adminSignInActivity.this,"Admin SignIn Successful",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(adminSignInActivity.this, adminDashboardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(adminSignInActivity.this,"Error checking data. Try Again Later",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }

            }
        });
    }
}