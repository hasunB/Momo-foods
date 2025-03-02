package com.example.momofoods;

import static com.example.momofoods.R.*;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.model.MailSender;
import com.example.momofoods.model.User;
import com.example.momofoods.model.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.mail.MessagingException;

public class SignUpActivity extends AppCompatActivity {

    public void gotoSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView signInText = findViewById(R.id.textView22);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSignIn();
            }
        });

        Button registerBtn = findViewById(R.id.button5);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText mobile = findViewById(R.id.editTextText3);
                EditText name = findViewById(R.id.editTextText4);
                EditText email = findViewById(R.id.editTextText5);
                EditText password = findViewById(R.id.editTextText6);

                String mobileText = mobile.getText().toString();
                String nameText = name.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if(mobileText.isEmpty()){
                    mobile.setError("Please enter mobile number");
                } else if(nameText.isEmpty()){
                    name.setError("Please enter name");
                } else if(emailText.isEmpty()){
                    email.setError("Please enter email");
                } else if(!Validations.isEmailValid(emailText)){
                    email.setError("Please enter valid email");
                } else if(passwordText.isEmpty()){
                    password.setError("Please enter password");
                } else if(!Validations.isPasswordValid(passwordText)){
                    password.setError("Please enter valid password");
                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").whereEqualTo("mobile",mobileText).get(Source.SERVER)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        if (!task.getResult().isEmpty()) {
                                            Toast.makeText(SignUpActivity.this, "Mobile Number Already Exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //generate verification code
                                            int code = (int) (Math.random() * 1000000);

                                            LocalDateTime currentDateTime = null;
                                            String formattedDateTime = null;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                currentDateTime = LocalDateTime.now();
                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                                formattedDateTime = currentDateTime.format(formatter);
                                            }

                                            User user = new User(nameText,formattedDateTime,"Enter Your Address",code,mobileText,passwordText,emailText);

                                            db.collection("users").add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                                            //sending verification email
                                                            new SendMailTask("","",emailText,"MomoFoods Verification Code",String.valueOf(code)).execute();

                                                            Intent intent = new Intent(SignUpActivity.this, UserVerificationActivity.class);
                                                            intent.putExtra("Verification",code);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUpActivity.this, "Registration Failed.Try Again Later", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } )
                                            ;
                                        }
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Error checking mobile number. Try Again Later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }


            }
        });
    }

    public class SendMailTask extends AsyncTask<Void, Void, Boolean> {
        private final MailSender mailSender;
        private final String to, subject, message;

        public SendMailTask(String email, String password, String to, String subject, String message) {
            this.mailSender = new MailSender(email, password);
            this.to = to;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                mailSender.sendEmail(to, subject, message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(SignUpActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


