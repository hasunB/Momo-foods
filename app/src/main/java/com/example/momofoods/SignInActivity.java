package com.example.momofoods;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.dto.User_DTO;
import com.example.momofoods.model.SQliteHelper;
import com.example.momofoods.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class SignInActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Button AdminSignIn;
    private boolean isLandscape = false;
    private boolean isColor = true;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //admin button with sensor control
        AdminSignIn = findViewById(R.id.button11);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        AdminSignIn.setEnabled(false);
        AdminSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, adminSignInActivity.class);
                startActivity(intent);
            }
        });

        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }
        }


        TextView registerText = findViewById(R.id.textView10);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
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
                    db.collection("users").whereEqualTo("mobile",mobileText).whereEqualTo("password",passwordText).get(Source.SERVER)
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if(task.isSuccessful() && task.getResult() != null){
                                                if(task.getResult().isEmpty()){
                                                    Toast.makeText(SignInActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                                                } else {
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                                    String mobile = documentSnapshot.getString("mobile");
                                                    String email = documentSnapshot.getString("email");
                                                    String name = documentSnapshot.getString("name");

                                                    SQliteHelper sQliteHelper = new SQliteHelper(SignInActivity.this,"Momofoods.db",null,1);

                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                                                            ContentValues contentValues = new ContentValues();
                                                            contentValues.put("mobile", mobile);
                                                            contentValues.put("email", email);
                                                            contentValues.put("name", name);

                                                            sqLiteDatabase.insert("user",null,contentValues);
                                                        }
                                                    }).start();

                                                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.momofoods", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putBoolean("isLoggedIn",true);
                                                    editor.apply();

                                                    Toast.makeText(SignInActivity.this,"SignIn Successful",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Toast.makeText(SignInActivity.this,"Error checking data. Try Again Later",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                }

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];

        if (Math.abs(x) > Math.abs(y)) {
            // Landscape Mode
            isLandscape = true;

            if (isLandscape){
                AdminSignIn.setBackgroundColor(Color.parseColor("#9F7A5F"));
                AdminSignIn.setEnabled(true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission., Manifest.permission.CALL_PHONE},
//                PERMISSION_REQUEST_CODE);
    }

    // Handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

//Abc@1234
//Xyz$5678
//P@ssw0rd9
//T3st!Abc
//Qw3rty$@
//1A@bC!d2
//L0g1n@Ok
//Pass!123
//Te$tinG8
//Try@987A