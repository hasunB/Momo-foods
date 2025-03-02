package com.example.momofoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.model.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AdminChangeEmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_change_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent  = getIntent();
        String id = intent.getStringExtra("employeeId");
        String name = intent.getStringExtra("employeeName");
        String nic = intent.getStringExtra("employeeNic");
        int role = Integer.parseInt(intent.getStringExtra("employeeRole"));
        String mobile = intent.getStringExtra("employeeMobile");
        String email = intent.getStringExtra("employeeEmail");
        String password = intent.getStringExtra("employeePassword");

        EditText editTextText39 = findViewById(R.id.editTextText39);
        editTextText39.setText(name);
        EditText editTextPhone2 = findViewById(R.id.editTextPhone2);
        editTextPhone2.setText(mobile);
        EditText editTextText38 = findViewById(R.id.editTextText38);
        editTextText38.setText(email);
        EditText editTextText37 = findViewById(R.id.editTextText37);
        editTextText37.setText(password);
        EditText editTextText33 = findViewById(R.id.editTextText33);
        editTextText33.setText(nic);

        RadioButton radioButton5 = findViewById(R.id.radioButton5);
        RadioButton radioButton6 = findViewById(R.id.radioButton6);
        RadioButton radioButton8 = findViewById(R.id.radioButton8);
        RadioButton radioButton9 = findViewById(R.id.radioButton9);

        int updateRole = 0;

        if(role == 2){
            radioButton5.setChecked(true);
            updateRole = 2;
        } else if(role == 3){
            radioButton6.setChecked(true);
            updateRole = 3;
        } else if(role == 4){
            radioButton8.setChecked(true);
            updateRole = 4;
        } else if(role == 5){
            radioButton9.setChecked(true);
            updateRole = 5;
        } else {
            radioButton5.setChecked(false);
            radioButton6.setChecked(false);
            radioButton8.setChecked(false);
            radioButton9.setChecked(false);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button updateEmployee = findViewById(R.id.button22);
        int finalUpdateRole = updateRole;
        updateEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextText39.getText().toString().isEmpty()){
                    editTextText39.setError("Name cannot be empty");
                    editTextText39.requestFocus();
                } else if(editTextPhone2.getText().toString().isEmpty()){
                    editTextPhone2.setError("Phone cannot be empty");
                    editTextPhone2.requestFocus();
                } else if(editTextText38.getText().toString().isEmpty()){
                    editTextText38.setError("Email cannot be empty");
                    editTextText38.requestFocus();
                } else if(editTextText33.getText().toString().isEmpty()){
                    editTextText33.setError("Password cannot be empty");
                    editTextText33.requestFocus();
                } else if(editTextText37.getText().toString().isEmpty()){
                    editTextText37.setError("ID cannot be empty");
                    editTextText37.requestFocus();
                } else if (!Validations.isEmailValid(editTextText38.getText().toString())) {
                    editTextText38.setError("Email is not valid");
                    editTextText38.requestFocus();
                } else if (!Validations.isPasswordValid(editTextText33.getText().toString())) {
                    editTextText33.setError("Password is must contain 8 characters, 1 uppercase, 1 lowercase, 1 number and 1 special character");
                    editTextText33.requestFocus();
                } else {

                    HashMap<String,Object> employees = new HashMap<>();
                    employees.put("name",editTextText39.getText().toString());
                    employees.put("phone",editTextPhone2.getText().toString());
                    employees.put("email",editTextText38.getText().toString());
                    employees.put("id",editTextText37.getText().toString());
                    employees.put("password",editTextText33.getText().toString());
                    employees.put("role", finalUpdateRole);

                    db.collection("employees").document(id).update(employees)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AdminChangeEmployeeActivity.this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(AdminChangeEmployeeActivity.this, adminDashboardActivity.class);
                                    startActivity(intent1);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AdminChangeEmployeeActivity.this, "Failed to update employee", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        ImageButton employeeDelete = findViewById(R.id.imageButton12);
        employeeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("employees").document(id).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AdminChangeEmployeeActivity.this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(AdminChangeEmployeeActivity.this, adminDashboardActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminChangeEmployeeActivity.this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}