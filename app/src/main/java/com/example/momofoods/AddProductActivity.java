package com.example.momofoods;

import android.app.ComponentCaller;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.momofoods.dto.Food_DTO;
import com.example.momofoods.dto.User_DTO;
import com.example.momofoods.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView foodImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText productNameEditText = findViewById(R.id.editTextText25);
        EditText productDescriptionEditText = findViewById(R.id.editTextText24);
        EditText priceEditText = findViewById(R.id.editTextText23);
        EditText qtyEditText = findViewById(R.id.editTextText10);
        EditText ratingEditText = findViewById(R.id.editTextText22);
        EditText caloriesEditText = findViewById(R.id.editTextText21);
        foodImageView = findViewById(R.id.imageView20);

        Button addImageButton = findViewById(R.id.button18);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        Button addProductButton = findViewById(R.id.button16);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){

                    if(productNameEditText.getText().toString().isEmpty()) {
                        productNameEditText.setError("Please Enter Product Name");
                    } else if(productDescriptionEditText.getText().toString().isEmpty()) {
                        productDescriptionEditText.setError("Please Enter Product Description");
                    } else if(priceEditText.getText().toString().isEmpty()) {
                        priceEditText.setError("Please Enter Price");
                    } else if(qtyEditText.getText().toString().isEmpty()) {
                        qtyEditText.setError("Please Enter Qty");
                    } else if(ratingEditText.getText().toString().isEmpty()) {
                        ratingEditText.setError("Please Enter Rating");
                    } else if(caloriesEditText.getText().toString().isEmpty()) {
                        caloriesEditText.setError("Please Enter Calories");
                    } else {

                        LocalDateTime currentDateTime = null;
                        String formattedDateTime = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            currentDateTime = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            formattedDateTime = currentDateTime.format(formatter);
                        }
                        Food_DTO foodDto = new Food_DTO(productNameEditText.getText().toString(),
                                productDescriptionEditText.getText().toString(),
                                priceEditText.getText().toString(),
                                "",
                                ratingEditText.getText().toString(),
                                caloriesEditText.getText().toString(),
                                Integer.parseInt(qtyEditText.getText().toString()),
                                "",
                                formattedDateTime);

                       db.collection("foods").add(foodDto)
                               .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                   @Override
                                   public void onComplete(@NonNull Task<DocumentReference> task) {
                                       String foodId = task.getResult().getId();
                                       Log.i("foodId",foodId);
                                       uploadImage(foodId);
                                   }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(AddProductActivity.this, "Product add Failed", Toast.LENGTH_SHORT).show();
                                   }
                               });

                    }

                } else {
                    Toast.makeText(AddProductActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
                }
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddProductActivity.this, "Image Upload Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddProductActivity.this, adminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(AddProductActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show());
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