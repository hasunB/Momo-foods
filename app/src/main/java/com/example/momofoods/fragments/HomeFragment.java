package com.example.momofoods.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.momofoods.AdvancedSearchActivity;
import com.example.momofoods.HomeActivity;
import com.example.momofoods.R;
import com.example.momofoods.SignInActivity;
import com.example.momofoods.SingleProductViewActivity;
import com.example.momofoods.dto.User_DTO;
import com.example.momofoods.model.Food;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SQliteHelper sQliteHelper = new SQliteHelper(getContext(), "Momofoods.db",null,1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase = sQliteHelper.getWritableDatabase();

                String name = "";
                Cursor cursor = null;
                try {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM `user`", null);

                    while (cursor.moveToNext()) {
                        name = cursor.getString(2);
                    }

                    final String finalname = name;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView24 = view.findViewById(R.id.textView24);
                            textView24.setText(finalname);
                        }
                    });


                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        }).start();

        EditText searchText = view.findViewById(R.id.editTextText40);

        ImageButton searchButton = view.findViewById(R.id.imageButton13);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchText.getText().toString().isEmpty()){
                    searchText.setError("Please enter a search query");
                } else {
                    Intent intent = new Intent(view.getContext(), AdvancedSearchActivity.class);
                    intent.putExtra("Query",searchText.getText().toString());
                    startActivity(intent);
                }

            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Food> foodList = new ArrayList<>();
        db.collection("foods").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(view.getContext(), "No foods Available", Toast.LENGTH_SHORT).show();
                            } else {
                                SQliteHelper sQliteHelper = new SQliteHelper(view.getContext(), "foods.db", null, 1);
                                SQLiteDatabase db = sQliteHelper.getWritableDatabase();

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String id = documentSnapshot.getId();
                                    String name = documentSnapshot.getString("productName");
                                    String description = documentSnapshot.getString("productDescription");
                                    String price = documentSnapshot.getString("price");
                                    String imageUrl = "https://www.google.com/"; // Default image URL
                                    String rating = documentSnapshot.getString("rating");
                                    String calories = documentSnapshot.getString("calories");
                                    int quantity = documentSnapshot.getLong("quantity").intValue();
                                    int category_id = 1;

                                    if (name != null && description != null && price != null && rating != null && calories != null) {
                                        try {
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("id", id);
                                            contentValues.put("name", name);
                                            contentValues.put("descrption", description);
                                            contentValues.put("price", Double.parseDouble(price));
                                            contentValues.put("rating", rating);
                                            contentValues.put("calories", calories);
                                            contentValues.put("qty", quantity);
                                            contentValues.put("categories_id", category_id);

                                            long result = db.replace("foods", null, contentValues);
                                            if (result == -1) {
                                                Log.e("Database", "Failed to insert product ID: " + id);
                                            } else {
                                                Log.d("Database", "Product inserted: " + id);
                                            }

                                            // Add to RecyclerView List
                                            Food food = new Food(id, name, description, Double.parseDouble(price), imageUrl, rating, calories, quantity, category_id);
                                            foodList.add(food);

                                        } catch (NumberFormatException e) {
                                            Log.e("Database", "Error parsing price: " + price, e);
                                        }
                                    } else {
                                        Log.e("Database", "Some fields are null, skipping insert for ID: " + id);
                                    }
                                }
                                // Update RecyclerView
                                FoodAdapter adapter = new FoodAdapter(foodList);
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(view.getContext(), "Error checking data. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        return view;
    }

}

class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder>{

    public FoodAdapter(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        public TextView PopularNowTextView1;

        public TextView PopularNowTextView2;

        public ImageView PopularNowImageView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            PopularNowTextView1 = itemView.findViewById(R.id.textView25);
            PopularNowTextView2 = itemView.findViewById(R.id.textView26);
            PopularNowImageView = itemView.findViewById(R.id.imageView3);
        }

    }

    public ArrayList<Food> foodList;

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout,parent,false);

        FoodViewHolder foodViewHolder = new FoodViewHolder(view);

        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.PopularNowTextView1.setText(foodList.get(position).getName());
        holder.PopularNowTextView2.setText(String.valueOf(foodList.get(position).getPrice()));
        Glide.with(holder.itemView.getContext()).load("https://limegreen-cattle-220394.hostingersite.com/uploads/"+foodList.get(position).getId()+".jpg").into(holder.PopularNowImageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SingleProductViewActivity.class);
            intent.putExtra("foodId",foodList.get(position).getId());
            intent.putExtra("foodName",foodList.get(position).getName());
            intent.putExtra("foodDescription",foodList.get(position).getDescription());
            intent.putExtra("foodPrice",String.valueOf(foodList.get(position).getPrice()));
            intent.putExtra("foodRating",foodList.get(position).getRating());
            intent.putExtra("foodCalories",foodList.get(position).getCalories());
            intent.putExtra("foodImageUrl","https://limegreen-cattle-220394.hostingersite.com/uploads/"+foodList.get(position).getId()+".jpg");
            intent.putExtra("foodQty",String.valueOf(foodList.get(position).getQty()));

            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return this.foodList.size();
    }
}