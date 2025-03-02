package com.example.momofoods;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class InvoiceActivity extends AppCompatActivity {

    public String userMobile = "";
    public String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SQliteHelper sQliteHelper = new SQliteHelper(InvoiceActivity.this, "Momofoods.db",null,1);

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

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        }).start();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String mobile = intent.getStringExtra("mobile");
        String address = intent.getStringExtra("address");
        double total = intent.getDoubleExtra("totalPrice", 0);
        double fee = intent.getDoubleExtra("fee", 0);
        int invoiceNo = intent.getIntExtra("invoiceNo", 0);

        TextView textView59 = findViewById(R.id.textView59);
        textView59.setText(String.valueOf(invoiceNo));

        TextView textView61 = findViewById(R.id.textView61);
        textView61.setText(String.format("LKR %s", String.valueOf(total)));

        TextView textView63 = findViewById(R.id.textView63);
        textView63.setText(String.format("LKR %s", String.valueOf(fee)));

        double subtotal = total - fee;

        TextView textView65 = findViewById(R.id.textView65);
        textView65.setText(String.format("LKR %s", String.valueOf(subtotal)));

        TextView textView67 = findViewById(R.id.textView67);

        ConstraintLayout trackOrderButton = findViewById(R.id.constraintLayout7);
        ImageButton gotoHomeButton = findViewById(R.id.imageButton8);

        if(fee == 0.0){
            textView67.setText("Payment Method : Dine In");
            trackOrderButton.setEnabled(false);
        } else if (fee == 300.00) {
            textView67.setText("Payment Method : Card");
        } else if (fee == 400.00) {
            textView67.setText("Payment Method : Cash on Delivery");
        } else {
            textView67.setText("Payment Method : None");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TableLayout tableLayout = findViewById(R.id.invoiceTableLayout1);

        db.collection("invoice").whereEqualTo("userMobile",userMobile).whereEqualTo("invoiceNo",String.valueOf(invoiceNo)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(InvoiceActivity.this,"invoice is Empty",Toast.LENGTH_SHORT).show();
                            } else {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String name = documentSnapshot.getString("name");
                                    int price = documentSnapshot.getLong("price").intValue();
                                    int qty = documentSnapshot.getLong("quantity").intValue();

                                    // Add to to table
                                    TableRow tableRow = new TableRow(InvoiceActivity.this);
                                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.WRAP_CONTENT
                                    ));

                                    TextView nameTextView = new TextView(InvoiceActivity.this);
                                    nameTextView.setText(name);
                                    nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    TextView priceTextView = new TextView(InvoiceActivity.this);
                                    priceTextView.setText(String.valueOf(price)+".0");
                                    priceTextView.setGravity(Gravity.END);
                                    priceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    TextView qtyTextView = new TextView(InvoiceActivity.this);
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
                            Toast.makeText(InvoiceActivity.this, "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        gotoHomeButton.setOnClickListener(view -> {
            Intent intent2 = new Intent(InvoiceActivity.this, MainActivity.class);
            startActivity(intent2);
        });

        trackOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(InvoiceActivity.this,FoodDelivery.class);
                startActivity(intent1);
            }
        });


    }


}