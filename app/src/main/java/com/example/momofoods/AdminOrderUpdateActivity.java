package com.example.momofoods;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminOrderUpdateActivity extends AppCompatActivity {

    private Button changeStatusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_order_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String mobile = intent.getStringExtra("mobile");
        String address = intent.getStringExtra("address");
        double total = intent.getDoubleExtra("totalPrice", 0);
        double fee = intent.getDoubleExtra("fee", 0);
        int invoiceNo = intent.getIntExtra("invoiceNo", 0);

        TextView userNameTextView = findViewById(R.id.textView93);
        userNameTextView.setText(String.valueOf(name));

        TextView userAddressTextView = findViewById(R.id.textView94);
        userAddressTextView.setText(String.valueOf(address));

        TextView userMobileTextView = findViewById(R.id.textView95);
        userMobileTextView.setText("Contact No: " + String.valueOf(mobile));

        TextView invoiceNoTextView = findViewById(R.id.textView97);
        invoiceNoTextView.setText(String.valueOf(invoiceNo));

        TextView totalTextView = findViewById(R.id.textView109);
        totalTextView.setText(String.format("LKR %s", String.valueOf(total)));

        TextView feeTextView = findViewById(R.id.textView106);
        feeTextView.setText(String.format("LKR %s", String.valueOf(fee)));

        double subtotal = total - fee;

        TextView subtotalTextView = findViewById(R.id.textView107);
        subtotalTextView.setText(String.format("LKR %s", String.valueOf(subtotal)));

        TextView textView110 = findViewById(R.id.textView110);

        if(fee == 0.0){
            textView110.setText("Payment Method : Dine In");
        } else if (fee == 300.00) {
            textView110.setText("Payment Method : Card");
        } else if (fee == 400.00) {
            textView110.setText("Payment Method : Cash on Delivery");
        } else {
            textView110.setText("Payment Method : None");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TableLayout tableLayout = findViewById(R.id.adminOrdertableLayout);

        db.collection("invoice").whereEqualTo("userMobile",mobile).whereEqualTo("invoiceNo",invoiceNo).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(AdminOrderUpdateActivity.this,"invoice is Empty",Toast.LENGTH_SHORT).show();
                            } else {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String name = documentSnapshot.getString("name");
                                    int price = documentSnapshot.getLong("price").intValue();
                                    int qty = documentSnapshot.getLong("quantity").intValue();

                                    int status = documentSnapshot.getLong("status").intValue();

                                    if(status == 1){
                                        changeButtonStatus("#EF5350","Preparing");
                                    } else if(status == 2){
                                        changeButtonStatus("#FBC02D","On the way");
                                    } else if (status == 3){
                                        changeButtonStatus("#7CB342","Delivered");
                                    } else if (status == 4){
                                        changeButtonStatus("#B71C1C","Completed");
                                        changeStatusButton.setEnabled(false);
                                    }

                                    // Add to to table
                                    TableRow tableRow = new TableRow(AdminOrderUpdateActivity.this);
                                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.WRAP_CONTENT
                                    ));

                                    TextView nameTextView = new TextView(AdminOrderUpdateActivity.this);
                                    nameTextView.setText(name);
                                    nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    TextView priceTextView = new TextView(AdminOrderUpdateActivity.this);
                                    priceTextView.setText(String.valueOf(price)+".0");
                                    priceTextView.setGravity(Gravity.END);
                                    priceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    TextView qtyTextView = new TextView(AdminOrderUpdateActivity.this);
                                    qtyTextView.setText(String.valueOf(qty));
                                    qtyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    // Add views to the row
                                    tableRow.addView(nameTextView);
                                    tableRow.addView(qtyTextView);
                                    tableRow.addView(priceTextView);

                                    tableLayout.addView(tableRow);

                                }


                            }
                        } else {
                            Toast.makeText(AdminOrderUpdateActivity.this, "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        changeStatusButton = findViewById(R.id.button12);
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("invoice").whereEqualTo("userMobile",mobile).whereEqualTo("invoiceNo",invoiceNo).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    if(task.getResult().isEmpty()){
                                        Toast.makeText(AdminOrderUpdateActivity.this,"invoice is Empty",Toast.LENGTH_SHORT).show();
                                    } else {

                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            String orderId = documentSnapshot.getId();
                                            int status = documentSnapshot.getLong("status").intValue();

                                            if(status == 1){
                                                db.collection("invoice").document(orderId).update("status",2);
                                                changeButtonStatus("#FBC02D","On the way");
                                            } else if (status == 2){
                                                db.collection("invoice").document(orderId).update("status",3);
                                                changeButtonStatus("#7CB342","Delivered");
                                            } else if (status == 3){
                                                db.collection("invoice").document(orderId).update("status",4);
                                                changeButtonStatus("#B71C1C","Completed");
                                                changeStatusButton.setEnabled(false);
                                            }

                                        }

                                    }
                                } else {
                                    Toast.makeText(AdminOrderUpdateActivity.this, "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        ImageButton orderDeleteButton = findViewById(R.id.imageButton9);
        orderDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("invoice").whereEqualTo("userMobile",mobile).whereEqualTo("invoiceNo",invoiceNo).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    if(task.getResult().isEmpty()){
                                        Toast.makeText(AdminOrderUpdateActivity.this,"invoice is Empty",Toast.LENGTH_SHORT).show();
                                    } else {

                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            String orderId = documentSnapshot.getId();

                                            db.collection("invoice").document(orderId).delete();

                                        }

                                        finish();
                                        Intent intent = new Intent(AdminOrderUpdateActivity.this, adminDashboardActivity.class);
                                        startActivity(intent);

                                    }
                                } else {
                                    Toast.makeText(AdminOrderUpdateActivity.this, "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    private void changeButtonStatus(String color,String text){

        changeStatusButton.setBackgroundColor(Color.parseColor(color));
        changeStatusButton.setText(text);

    }
}