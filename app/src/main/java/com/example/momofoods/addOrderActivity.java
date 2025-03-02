package com.example.momofoods;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momofoods.model.Cart;
import com.example.momofoods.model.Food;
import com.example.momofoods.model.Invoice;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class addOrderActivity extends AppCompatActivity {

    public ArrayList<Food> addOrderList = new ArrayList<>();
    private double total;
    private TextView grandtotalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int code = (int) (Math.random() * 1000000);

        TextView orderNumberTextView = findViewById(R.id.textView98);
        orderNumberTextView.setText(String.valueOf(code));

        RadioButton dineInRadioButton = findViewById(R.id.radioButton);
        dineInRadioButton.setChecked(true);
        RadioButton cashOnDeliveryRadioButton = findViewById(R.id.radioButton4);

        TextView feeTextView = findViewById(R.id.textView138);

        EditText nameEditText = findViewById(R.id.editTextText12);
        EditText addressEditText = findViewById(R.id.editTextText18);
        EditText mobileEditText = findViewById(R.id.editTextText19);

        final double[] fee = new double[1];

        final double[] lastFee = {0.0};

        cashOnDeliveryRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dineInRadioButton.setChecked(false);
                cashOnDeliveryRadioButton.setChecked(true);

                total = total - lastFee[0] + 400.0;

                feeTextView.setText("400.0");
                fee[0] = 400.0;
                lastFee[0] = 400.0; // Update last fee

                grandtotalTextView.setText("LKR " + total);
            }
        });

        dineInRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dineInRadioButton.setChecked(true);
                cashOnDeliveryRadioButton.setChecked(false);

                total = total - lastFee[0] + 0.0;

                feeTextView.setText("0.0");
                fee[0] = 0.0;
                lastFee[0] = 0.0; // Update last fee

                grandtotalTextView.setText("LKR " + total);
            }
        });


        Button addProductToInvoiceButton = findViewById(R.id.button17);
        addProductToInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        Button addOrderButton = findViewById(R.id.button13);
        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addOrderList.size() == 0){
                    Toast.makeText(addOrderActivity.this, "No Food Selected", Toast.LENGTH_SHORT).show();
                } else if (nameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(addOrderActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (addressEditText.getText().toString().isEmpty()) {
                    Toast.makeText(addOrderActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();
                } else if (mobileEditText.getText().toString().isEmpty()) {
                    Toast.makeText(addOrderActivity.this, "Enter Mobile", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    LocalDateTime currentDateTime = null;
                    String formattedDateTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        formattedDateTime = currentDateTime.format(formatter);
                    }

                    for (Food food : addOrderList) {
                        Invoice invoice = new Invoice(code,
                                food.getQty(),
                                (int) food.getPrice(),
                                food.getDescription(),
                                food.getName(),
                                food.getId(),
                                0.0,
                                total,
                                addressEditText.getText().toString(),
                                nameEditText.getText().toString(),
                                mobileEditText.getText().toString(),
                                formattedDateTime,
                                1
                        );

                        db.collection("invoice").add(invoice)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Log.e("FireStoreError", "added documents");
                                        Toast.makeText(addOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(addOrderActivity.this, adminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("FireStoreError", "Error adding documents", e);
                                    }
                                });
                    }

                }
            }
        });


    }

    private void loadProductTable() {
        TableLayout tableLayout = findViewById(R.id.showproductTable);
        TextView totalTextView = findViewById(R.id.textView139);
        grandtotalTextView = findViewById(R.id.textView141);
        int tabelsize = addOrderList.size();

        total = 0.0;

        int rowCount = tableLayout.getChildCount();
        if (rowCount > 1) {
            tableLayout.removeViews(1, rowCount - 1);
        }

        for (Food food : addOrderList) {
            TableRow row = new TableRow(this);

            TextView productName = new TextView(this);
            productName.setText(food.getName());
            productName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

            TextView productQty = new TextView(this);
            productQty.setText(String.valueOf(food.getQty()));
            productQty.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

            TextView productPrice = new TextView(this);
            productPrice.setText("LKR " + (food.getPrice() * food.getQty()));
            productPrice.setGravity(Gravity.END);
            productPrice.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

            // Add views to the row
            row.addView(productName);
            row.addView(productQty);
            row.addView(productPrice);

            tableLayout.addView(row);

            total += food.getPrice() * food.getQty();
        }

        Log.i("Subtotal", "Total Price: LKR " + total);
        totalTextView.setText(String.valueOf(total));
        grandtotalTextView.setText("LKR "+String.valueOf(total));
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        Button closeButton = dialogView.findViewById(R.id.button15);
        RecyclerView recyclerView = dialogView.findViewById(R.id.dialogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Food> foodList = new ArrayList<>();

        db.collection("foods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().isEmpty()) {
                    Toast.makeText(addOrderActivity.this, "No foods Available", Toast.LENGTH_SHORT).show();
                } else {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = documentSnapshot.getId();
                        String name = documentSnapshot.getString("name");
                        String description = documentSnapshot.getString("description");
                        String price = documentSnapshot.getString("price");
                        String imageUrl = "https://www.google.com/"; // Default image URL
                        String rating = documentSnapshot.getString("rating");
                        String calories = documentSnapshot.getString("calories");
                        int quantity = documentSnapshot.getLong("quantity").intValue();
                        int category_id = 1;

                        // Add to RecyclerView List
                        Food food = new Food(id, name, description, Double.parseDouble(price), imageUrl, rating, calories, quantity, category_id);
                        foodList.add(food);

                    }

                    Log.i("foodList", String.valueOf(foodList.size()));

                    // Update RecyclerView
                    OrderProductAdapter adapter = new OrderProductAdapter(foodList, addOrderActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(addOrderActivity.this, "Error checking data. Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                loadProductTable();
            }
        });
    }


}

class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>{

    private ArrayList<Food> foodList;
    private addOrderActivity activity; // Store reference to activity

    public OrderProductAdapter(ArrayList<Food> foodList, addOrderActivity activity) {
        this.foodList = foodList;
        this.activity = activity;
    }

    class OrderProductViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;

        public TextView productQuantity;

        public TextView productPrice;

        public EditText productQty;

        public ImageButton addButton;

        public OrderProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textView100);
            productQuantity = itemView.findViewById(R.id.textView101);
            productPrice = itemView.findViewById(R.id.textView102);
            productQty = itemView.findViewById(R.id.editTextText20);
            addButton = itemView.findViewById(R.id.imageButton10);
        }

    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_order_product_layout,parent,false);

        OrderProductViewHolder foodViewHolder = new OrderProductViewHolder(view);

        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        holder.productName.setText(foodList.get(position).getName());
        holder.productQuantity.setText("Avaliability "+ String.valueOf(foodList.get(position).getQty()));
        holder.productPrice.setText("LKR "+ String.valueOf(foodList.get(position).getPrice()));

        InputFilter quantityFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                try {
                    // Combine the old text with the new input
                    String newInput = spanned.subSequence(0, i2) + charSequence.toString() + spanned.subSequence(i3, spanned.length());

                    // Convert the input to an integer
                    int input = Integer.parseInt(newInput);

                    // Allow only values between 1 and 10
                    if (input >= 1 && input <= foodList.get(position).getQty()) {
                        return null; // Acceptable input
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }

                return "";

            }
        };

        holder.productQty.setFilters(new InputFilter[]{quantityFilter});

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.addButton.setBackgroundColor(Color.parseColor("#B71C1C"));

                int quantity = Integer.parseInt(holder.productQty.getText().toString());

                Food food = new Food(foodList.get(position).getId(),
                        foodList.get(position).getName(),
                        foodList.get(position).getDescription(),
                        foodList.get(position).getPrice(),
                        foodList.get(position).getImageUrl(),
                        foodList.get(position).getRating(),
                        foodList.get(position).getCalories(),
                        quantity,
                        foodList.get(position).getCategories_id()
                );

                activity.addOrderList.add(food);
                Toast.makeText(activity, food.getName() + " added!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.foodList.size();
    }
}

