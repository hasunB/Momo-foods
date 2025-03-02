package com.example.momofoods.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momofoods.CheckOutActivity;
import com.example.momofoods.InvoiceActivity;
import com.example.momofoods.R;
import com.example.momofoods.SignInActivity;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;

public class profileFragment extends Fragment {

    public String userMobile = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQliteHelper sQliteHelper = new SQliteHelper(getContext(), "Momofoods.db",null,1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                String mobile = "";
                String name = "";
                Cursor cursor = null;
                try {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM `user`", null);

                    while (cursor.moveToNext()) {
                        mobile = cursor.getString(0);
                        name = cursor.getString(2);
                    }

                    userMobile = mobile;

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        }).start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        EditText editTextText13 = view.findViewById(R.id.editTextText13);
        EditText editTextText14 = view.findViewById(R.id.editTextText14);
        EditText editTextText15 = view.findViewById(R.id.editTextText15);
        EditText editTextText16 = view.findViewById(R.id.editTextText16);
        EditText editTextText17 = view.findViewById(R.id.editTextText17);

        editTextText15.setEnabled(false);
        editTextText16.setEnabled(false);

        final String[] userId = {""};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("mobile",userMobile).get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().isEmpty()) {
                                Log.d("FireStore", "No documents found");
                            } else {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    userId[0] = documentSnapshot.getId();
                                    String userAddress = documentSnapshot.getString("address");
                                    String userName = documentSnapshot.getString("name");
                                    String userPassword = documentSnapshot.getString("password");
                                    String userEmail = documentSnapshot.getString("email");

                                    editTextText13.setText(userAddress);
                                    editTextText14.setText(userPassword);
                                    editTextText15.setText(userMobile);
                                    editTextText16.setText(userEmail);
                                    editTextText17.setText(userName);
                                }
                            }
                        } else {
                            Log.e("FireStoreError", "Error getting documents", task.getException());
                        }
                    }
                });

        Button updateUserButton = view.findViewById(R.id.button10);
        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> documentData = new HashMap<>();
                documentData.put("address", editTextText13.getText().toString());
                documentData.put("password", editTextText14.getText().toString());
                documentData.put("name", editTextText17.getText().toString());

                db.collection("users").document(userId[0]).update(documentData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "User Updated Successfully", Toast.LENGTH_SHORT).show();

                                SQliteHelper sQliteHelper = new SQliteHelper(getContext(), "Momofoods.db",null,1);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                                        sqLiteDatabase.execSQL("UPDATE `user` SET `name` = '" + editTextText17.getText().toString() + "' WHERE `mobile` = '" + userMobile + "'");
                                    }
                                }).start();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "User Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button LogOutButton = view.findViewById(R.id.button14);
        LogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQliteHelper sQliteHelper = new SQliteHelper(getContext(), "Momofoods.db",null,1);

                SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                sqLiteDatabase.execSQL("DELETE FROM `user`");

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.example.momofoods", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                getActivity().finish();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);

            }
        });


        return view;

    }

}