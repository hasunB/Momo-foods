package com.example.momofoods;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.momofoods.dto.Cart_DTO;
import com.example.momofoods.model.Cart;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SingleProductViewActivity extends AppCompatActivity {

    private String userMobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SQliteHelper sQliteHelper = new SQliteHelper(SingleProductViewActivity.this, "Momofoods.db",null,1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                String mobile = "";
                Cursor cursor = null;
                try {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM `user`", null);

                    while (cursor.moveToNext()) {
                        mobile = cursor.getString(0);
                    }

                    userMobile = mobile;

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        }).start();

        //get Product data
        Intent intent = getIntent();
        String foodId = intent.getStringExtra("foodId");
        String foodName = intent.getStringExtra("foodName");
        String foodDescription = intent.getStringExtra("foodDescription");
        String foodPrice = intent.getStringExtra("foodPrice");
        String foodImageUrl = intent.getStringExtra("foodImageUrl");
        String foodRating = intent.getStringExtra("foodRating");
        String foodCalories = intent.getStringExtra("foodCalories");
        int foodQty = Integer.parseInt(intent.getStringExtra("foodQty"));

        //back button
        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener(view -> finish());

        //goto cart button
        ImageButton gotoCartButton = findViewById(R.id.imageButton2);
        gotoCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleProductViewActivity.this, HomeActivity.class);
                intent.putExtra("fragment","cart");
                startActivity(intent);
            }
        });

        ImageView foodImageView = findViewById(R.id.imageView2);
        Glide.with(this).load(foodImageUrl).into(foodImageView);

        //set name
        TextView foodNameTextView = findViewById(R.id.textView11);
        foodNameTextView.setText(foodName);

        //set price
        TextView foodPriceTextView = findViewById(R.id.textView12);
        foodPriceTextView.setText("LKR "+ foodPrice);

        //set description
        TextView foodDescriptionTextView = findViewById(R.id.textView30);
        foodDescriptionTextView.setText(foodDescription);

        //set quantity
        EditText quantityEditText = findViewById(R.id.editTextText7);
        quantityEditText.setText("1");

        //set rating
        TextView foodRatingTextView = findViewById(R.id.textView14);
        foodRatingTextView.setText("("+foodRating+")");

        //set calories
        TextView foodCaloriesTextView = findViewById(R.id.textView17);
        foodCaloriesTextView.setText(foodCalories+"Kcal");

        InputFilter quantityFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                try {
                    // Combine the old text with the new input
                    String newInput = spanned.subSequence(0, i2) + charSequence.toString() + spanned.subSequence(i3, spanned.length());

                    // Convert the input to an integer
                    int input = Integer.parseInt(newInput);

                    // Allow only values between 1 and 10
                    if (input >= 1 && input <= foodQty) {
                        return null; // Acceptable input
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }

                return "";

            }
        };

        quantityEditText.setFilters(new InputFilter[]{quantityFilter});

        ImageButton quantityAddButton = findViewById(R.id.quantitySubButton1);
        quantityAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                if(quantity > 1){
                    quantity--;
                    quantityEditText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(SingleProductViewActivity.this,"Quantity cannot be less than 1",Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageButton quantitySubButton = findViewById(R.id.quantityAddButton2);
        quantitySubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                if(quantity < foodQty){
                    quantity++;
                    quantityEditText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(SingleProductViewActivity.this,"Quantity cannot be more than 10",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button addToCartButton = findViewById(R.id.button3);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //quantity update
                db.collection("cart").whereEqualTo("productId",foodId).whereEqualTo("userMobile",userMobile).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    if (!task.getResult().isEmpty()) {

                                        String cartItemId = task.getResult().getDocuments().get(0).getId();

                                        int productQty = task.getResult().getDocuments().get(0).getLong("quantity").intValue();

                                        Toast.makeText(SingleProductViewActivity.this, cartItemId, Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> data = new HashMap<>();
                                        try {
                                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                                            int newQty = productQty + quantity;
                                            data.put("quantity", newQty);

                                            db.collection("cart").document(cartItemId).update(data)
                                                    .addOnSuccessListener(unused ->
                                                            Toast.makeText(SingleProductViewActivity.this, "Quantity Updated", Toast.LENGTH_SHORT).show()
                                                    )
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(SingleProductViewActivity.this, "Failed to Update Quantity", Toast.LENGTH_SHORT).show()
                                                    );

                                        } catch (NumberFormatException e) {
                                            Toast.makeText(SingleProductViewActivity.this, "Invalid Quantity", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        Cart_DTO cart = new Cart_DTO(foodId,foodName,foodDescription,Double.parseDouble(foodPrice),"/image/",foodRating,foodCalories,Integer.parseInt(quantityEditText.getText().toString()),userMobile);

                                        db.collection("cart").add(cart)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(SingleProductViewActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SingleProductViewActivity.this, "Product Adding Failed.Try Again Later", Toast.LENGTH_SHORT).show();
                                                    }
                                                } )
                                        ;
                                    }
                                } else {
                                    Toast.makeText(SingleProductViewActivity.this, "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


    }


}