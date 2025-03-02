package com.example.momofoods;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.dto.Employees_DTO;
import com.example.momofoods.model.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameEditText = findViewById(R.id.editTextText32);
        EditText phoneEditText = findViewById(R.id.editTextPhone);
        EditText emailEditText = findViewById(R.id.editTextText34);
        EditText passwordEditText = findViewById(R.id.editTextText36);
        EditText idEditText = findViewById(R.id.editTextText35);

        RadioButton waiterRadioButton = findViewById(R.id.radioButton5);
        waiterRadioButton.setChecked(true);
        RadioButton chefRadioButton = findViewById(R.id.radioButton6);
        RadioButton cashierRadioButton = findViewById(R.id.radioButton8);
        RadioButton deliveryRadioButton = findViewById(R.id.radioButton9);

        int emplyeesRole = 0;

        if (waiterRadioButton.isChecked()) {
            emplyeesRole = 2;
        } else if (chefRadioButton.isChecked()) {
            emplyeesRole = 3;
        } else if (cashierRadioButton.isChecked()) {
            emplyeesRole = 4;
        } else if (deliveryRadioButton.isChecked()) {
            emplyeesRole = 5;
        } else {
            emplyeesRole = 6;
        }

        Button addEmployeeButton = findViewById(R.id.button19);
        int finalEmplyeesRole = emplyeesRole;
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameEditText.getText().toString().isEmpty()){
                    nameEditText.setError("Name cannot be empty");
                    nameEditText.requestFocus();
                } else if(phoneEditText.getText().toString().isEmpty()){
                    phoneEditText.setError("Phone cannot be empty");
                    phoneEditText.requestFocus();
                } else if(emailEditText.getText().toString().isEmpty()){
                    emailEditText.setError("Email cannot be empty");
                    emailEditText.requestFocus();
                } else if(passwordEditText.getText().toString().isEmpty()){
                    passwordEditText.setError("Password cannot be empty");
                    passwordEditText.requestFocus();
                } else if(idEditText.getText().toString().isEmpty()){
                    idEditText.setError("ID cannot be empty");
                    idEditText.requestFocus();
                } else if (!Validations.isEmailValid(emailEditText.getText().toString())) {
                    nameEditText.setError("Email is not valid");
                    nameEditText.requestFocus();
                } else if (!Validations.isPasswordValid(passwordEditText.getText().toString())) {
                    passwordEditText.setError("Password is must contain 8 characters, 1 uppercase, 1 lowercase, 1 number and 1 special character");
                    passwordEditText.requestFocus();
                } else if(finalEmplyeesRole == 0){
                    Toast.makeText(AddUserActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                } else {

                    LocalDateTime currentDateTime = null;
                    String formattedDateTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        formattedDateTime = currentDateTime.format(formatter);
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Employees_DTO employees = new Employees_DTO(
                            nameEditText.getText().toString(),
                            phoneEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            idEditText.getText().toString(),
                            finalEmplyeesRole,
                            passwordEditText.getText().toString(),
                            formattedDateTime
                    );

                    db.collection("employees").add(employees)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(AddUserActivity.this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddUserActivity.this, adminDashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddUserActivity.this, "Failed to add employee", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }
        });

    }
}