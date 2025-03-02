package com.example.momofoods;

import android.app.ComponentCaller;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminChangeProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView foodImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_change_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String foodId = intent.getStringExtra("foodId");
        String foodName = intent.getStringExtra("foodName");
        String foodDescription = intent.getStringExtra("foodDescription");
        String foodPrice = intent.getStringExtra("foodPrice");
        String foodImageUrl = intent.getStringExtra("foodImageUrl");
        String foodRating = intent.getStringExtra("foodRating");
        String foodCalories = intent.getStringExtra("foodCalories");
        int foodQty = Integer.parseInt(intent.getStringExtra("foodQty"));

        //set name
        EditText foodNameEditText = findViewById(R.id.editTextText26);
        foodNameEditText.setText(foodName);

        //set description
        EditText foodDescriptionEditText = findViewById(R.id.editTextText27);
        foodDescriptionEditText.setText(foodDescription);

        //set price
        EditText foodPriceEditText = findViewById(R.id.editTextText28);
        foodPriceEditText.setText(foodPrice);

        //set qty
        EditText foodQtyEditText = findViewById(R.id.editTextText29);
        foodQtyEditText.setText(String.valueOf(foodQty));

        //set rating
        EditText foodRatingEditText = findViewById(R.id.editTextText30);
        foodRatingEditText.setText(foodRating);

        //set calories
        EditText foodCaloriesEditText = findViewById(R.id.editTextText31);
        foodCaloriesEditText.setText(foodCalories);

        //set image
        foodImageView = findViewById(R.id.imageView21);
        String ImageUrl = "https://limegreen-cattle-220394.hostingersite.com/uploads/"+foodId+".jpg";
        Glide.with(this).load(ImageUrl).into(foodImageView);

        InputFilter ratingFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    String newInput = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                    if (newInput.isEmpty()) {
                        return null;
                    }
                    float rating = Float.parseFloat(newInput);

                    if (rating >= 0.0 && rating <= 5.0) {
                        if (newInput.matches("^\\d*\\.?\\d{0,1}$")) {
                            return null; // Accept input
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
                return ""; // Reject invalid input
            }
        };

        foodRatingEditText.setFilters(new InputFilter[]{ratingFilter});

        Button changeButton = findViewById(R.id.button21);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button updateProductButton = findViewById(R.id.button20);
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("foods").document(foodId).update(
                        "productName", foodNameEditText.getText().toString(),
                        "productDescription", foodDescriptionEditText.getText().toString(),
                        "price", foodPriceEditText.getText().toString(),
                        "quantity", Integer.valueOf(foodQtyEditText.getText().toString()),
                        "rating", foodRatingEditText.getText().toString(),
                        "calories", foodCaloriesEditText.getText().toString()
                )
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AdminChangeProductActivity.this,"Product updated",Toast.LENGTH_LONG).show();
                                uploadImage(foodId);
                                Intent intent = new Intent(AdminChangeProductActivity.this, adminDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminChangeProductActivity.this,"Product update failed",Toast.LENGTH_LONG).show();
                            }
                        })
                ;


            }
        });

        ImageButton deleteProductImageButton = findViewById(R.id.imageButton11);
        deleteProductImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("foods").document(foodId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AdminChangeProductActivity.this,"product deleted successfully",Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(AdminChangeProductActivity.this, adminDashboardActivity.class);
                                startActivity(intent2);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminChangeProductActivity.this,"product deleted failed",Toast.LENGTH_SHORT).show();
                            }
                        })
                        ;
            }
        });

    }

    private void uploadImage(String productId){

        String uploadUrl = "https://limegreen-cattle-220394.hostingersite.com/storeImage.php";

        if(imageUri != null){
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] imageBytes = getBytesFromInputStream(inputStream);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody fileBody = RequestBody.create(imageBytes, MediaType.parse("image/*"));

                        MultipartBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("product_id", productId)
                                .addFormDataPart("image", "image.jpg", fileBody)
                                .build();

                        Request request = new Request.Builder()
                                .url(uploadUrl)
                                .post(requestBody)
                                .build();

                        try {

                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(AdminChangeProductActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show());
                            } else {
                                runOnUiThread(() -> Toast.makeText(AdminChangeProductActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }).start();


            } catch (IOException e) {
               e.printStackTrace();
               Toast.makeText(this, "Failed to read image!", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            foodImageView.setImageURI(imageUri);
        }
    }
}