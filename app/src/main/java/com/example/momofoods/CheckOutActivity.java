package com.example.momofoods;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.model.Invoice;
import com.example.momofoods.model.SQliteHelper;
import com.example.momofoods.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckOutActivity extends AppCompatActivity {

    public String userMobile = "";
    public String userName = "";
    public String userAddress = "";
    private EditText editTextText11;
    private Boolean isCompleted = false;
    private int invoiceNo;
    private static Double[] totalPrice = {};
    private double Cardfee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SQliteHelper sQliteHelper = new SQliteHelper(CheckOutActivity.this, "Momofoods.db",null,1);

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
                    userName = name;

                    final String finalname = name;
                    final String finalmobile = mobile;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView24 = findViewById(R.id.textView44);
                            textView24.setText(finalname);
                            TextView textView45 = findViewById(R.id.textView45);
                            textView45.setText("Contact No: "+finalmobile);
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
                                                        String address = documentSnapshot.getString("address");
                                                        userAddress = address;
                                                        editTextText11 = findViewById(R.id.editTextText11);
                                                        editTextText11.setText(userAddress);
                                                        Log.d("FireStore", "address: " + address);
                                                    }
                                                }
                                            } else {
                                                Log.e("FireStoreError", "Error getting documents", task.getException());
                                            }
                                        }
                                    });
                        }
                    });

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        }).start();


        Intent intent = getIntent();
        String TotalPrice1 =intent.getStringExtra("totalPrice");
        double totalPrice1 = Double.parseDouble(TotalPrice1);
        totalPrice = new Double[]{totalPrice1};
        Cardfee = 300.00;
        double CashFee = 400.00;

        TextView textView48 = findViewById(R.id.textView48);
        textView48.setText("LKR "+ String.valueOf(totalPrice[0]));

        RadioButton DineRadioButton = findViewById(R.id.radioButton1);
        RadioButton CardRadioButton = findViewById(R.id.radioButton2);
        RadioButton CashRadioButton = findViewById(R.id.radioButton3);

        DineRadioButton.setClickable(false);
        CardRadioButton.setClickable(false);
        CashRadioButton.setClickable(false);

        ConstraintLayout dineInLayout = findViewById(R.id.constraintLayout6);
        ConstraintLayout cardLayout = findViewById(R.id.constraintLayout5);
        ConstraintLayout cashLayout = findViewById(R.id.constraintLayout3);

        TextView textView47 = findViewById(R.id.textView47);

        //default check
        DineRadioButton.setChecked(true);
        final Boolean[] isCardFeeAdded = {false};
        final Boolean[] isCashFeeAdded = {false};

        dineInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DineRadioButton.setChecked(true);
                CardRadioButton.setChecked(false);
                CashRadioButton.setChecked(false);
                textView47.setText("Enjoy Your Dine In with Us");
                editTextText11.setEnabled(false);
            }
        });

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DineRadioButton.setChecked(false);
                CardRadioButton.setChecked(true);
                CashRadioButton.setChecked(false);
                isCardFeeAdded[0] = true;
                if (!isCashFeeAdded[0]){
                    totalPrice[0] = totalPrice[0] + Cardfee;
                } else {
                    totalPrice[0] = totalPrice[0] - CashFee;
                    totalPrice[0] = totalPrice[0] + Cardfee;
                    isCashFeeAdded[0] = false;
                }
                textView47.setText("+ LKR 300.00 (Delivery Fee)");
                editTextText11.setEnabled(true);
            }
        });

        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DineRadioButton.setChecked(false);
                CardRadioButton.setChecked(false);
                CashRadioButton.setChecked(true);
                textView47.setText("+ LKR 400.00 (Delivery Fee)");
                isCashFeeAdded[0] = true;
                if (!isCardFeeAdded[0]){
                    totalPrice[0] = totalPrice[0] + Cardfee;
                } else {
                    totalPrice[0] = totalPrice[0] - Cardfee;
                    totalPrice[0] = totalPrice[0] + CashFee;
                    isCardFeeAdded[0] = false;
                }
                editTextText11.setEnabled(true);
            }
        });

        //generate verification code
        invoiceNo = (int) (Math.random() * 1000000);

        Button placeOrder = findViewById(R.id.button9);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DineRadioButton.isChecked()){
                    //add to database
                    addedToDataBase(userMobile,userName,userAddress,totalPrice[0],0.0,invoiceNo);
                } else if(CashRadioButton.isChecked()){

                    editTextText11.setEnabled(true);
                    if (!editTextText11.getText().toString().isEmpty()){;
                        //add to database
                        addedToDataBase(userMobile,userName,editTextText11.getText().toString(),totalPrice[0],CashFee,invoiceNo);
                    } else {
                        editTextText11.setError("Please Enter Your Address");
                    }

                } else if(CardRadioButton.isChecked()){

                    editTextText11.setEnabled(true);
                    if (!editTextText11.getText().toString().isEmpty()){
                        //payment gateway
                        paymentGateway(String.valueOf(invoiceNo),totalPrice[0]);
                    } else {
                        editTextText11.setError("Please Enter Your Address");
                    }
                    
                }  else {
                    Toast.makeText(CheckOutActivity.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void paymentGateway(String OrderId,double price) {
        InitRequest req = new InitRequest();
        req.setMerchantId("1229645");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(price);             // Final Amount to be charged
        req.setOrderId(OrderId);        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName(userName);
        req.getCustomer().setLastName("");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone(userMobile);
        req.getCustomer().getAddress().setAddress(userAddress);
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, 1234); //unique request ID e.g. "11001"
    }

    public void addedToDataBase(String userMobile, String userName, String address, double TotalPrice, double fee, int InvoiceNo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .whereEqualTo("userMobile", userMobile)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String id = documentSnapshot.getString("productId");
                            String name = documentSnapshot.getString("name");
                            String description = documentSnapshot.getString("description");
                            int price = documentSnapshot.getLong("price").intValue();
                            int qty = documentSnapshot.getLong("quantity").intValue();

                            LocalDateTime currentDateTime = null;
                            String formattedDateTime = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                currentDateTime = LocalDateTime.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                formattedDateTime = currentDateTime.format(formatter);
                            }

                            Invoice invoice = new Invoice(InvoiceNo,qty,price,description,name,id,fee,TotalPrice,address,userName,userMobile,formattedDateTime,1);

                            db.collection("invoice").add(invoice)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Log.e("FireStoreError", "added documents");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("FireStoreError", "Error adding documents", e);
                                        }
                                    });

                        }

                        updateCart(userMobile);

                        Toast.makeText(CheckOutActivity.this, "Your Order has been Successfully placed", Toast.LENGTH_SHORT).show();
                        generateInvoice(userName,userMobile,userAddress,TotalPrice,fee,InvoiceNo);

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FireStoreError", "Error deleting documents", e);
                    }
                });
    }

    boolean updateCartIsCompleted = false;
    public void updateCart(String userMobile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .whereEqualTo("userMobile", userMobile)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("cart").document(document.getId()).delete();
                    }
                    updateCartIsCompleted = true;
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreError", "Error deleting documents", e);
                });
    }

    public void generateInvoice(String Name, String Mobile, String Address, double TotalPrice, double fee, int invoiceNo) {
        Intent intent = new Intent(CheckOutActivity.this, InvoiceActivity.class);
        intent.putExtra("name",Name);
        intent.putExtra("mobile",Mobile);
        intent.putExtra("address",Address);
        intent.putExtra("totalPrice",TotalPrice);
        intent.putExtra("fee",fee);
        intent.putExtra("invoiceNo",invoiceNo);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().toString();
                        //add to database
                        addedToDataBase(userMobile,userName,userAddress,totalPrice[0],Cardfee,invoiceNo);
                    }else {
                        msg = "Result:" + response.toString();
                    }
                }else {
                    msg = "Result: no response";
                    Log.d("Payment Response", msg);
                    Toast.makeText(CheckOutActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(CheckOutActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CheckOutActivity.this, "User Canceled the Request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}