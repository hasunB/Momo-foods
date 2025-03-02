package com.example.momofoods;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.momofoods.model.Food;
import com.example.momofoods.model.SQliteHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdvancedSearchActivity extends AppCompatActivity {

    private RadioButton priceHighToLow;
    private RadioButton priceLowToHigh;
    private RadioButton quantityHighToLow;
    private RadioButton quantityLowToHigh;
    private RadioButton ratingHighToLow;
    private RadioButton ratingLowToHigh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton goBackButton = findViewById(R.id.imageButton14);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Closes the current activity and goes back
            }
        });

        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");

        Log.i("searchQuery",query);

        TextView resultTextQuery = findViewById(R.id.textView21);
        resultTextQuery.setText("Results For: " + query);

        RecyclerView recyclerView = findViewById(R.id.advancedSearchRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        SQliteHelper sQliteHelper = new SQliteHelper(AdvancedSearchActivity.this,"foods.db",null,1);


        priceHighToLow = findViewById(R.id.radioButton7);
        priceLowToHigh = findViewById(R.id.radioButton10);
        quantityHighToLow = findViewById(R.id.radioButton15);
        quantityLowToHigh = findViewById(R.id.radioButton14);
        ratingHighToLow = findViewById(R.id.radioButton12);
        ratingLowToHigh = findViewById(R.id.radioButton13);

        fetchFilteredProducts(sQliteHelper, query, recyclerView);

        priceLowToHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(false);
                priceLowToHigh.setChecked(true);
                quantityHighToLow.setChecked(false);
                quantityLowToHigh.setChecked(false);
                ratingHighToLow.setChecked(false);
                ratingLowToHigh.setChecked(false);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });

        priceHighToLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(true);
                priceLowToHigh.setChecked(false);
                quantityHighToLow.setChecked(false);
                quantityLowToHigh.setChecked(false);
                ratingHighToLow.setChecked(false);
                ratingLowToHigh.setChecked(false);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });

        quantityHighToLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(false);
                priceLowToHigh.setChecked(false);
                quantityHighToLow.setChecked(true);
                quantityLowToHigh.setChecked(false);
                ratingHighToLow.setChecked(false);
                ratingLowToHigh.setChecked(false);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });

        quantityLowToHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(false);
                priceLowToHigh.setChecked(false);
                quantityHighToLow.setChecked(false);
                quantityLowToHigh.setChecked(true);
                ratingHighToLow.setChecked(false);
                ratingLowToHigh.setChecked(false);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });

        ratingHighToLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(false);
                priceLowToHigh.setChecked(false);
                quantityHighToLow.setChecked(false);
                quantityLowToHigh.setChecked(false);
                ratingHighToLow.setChecked(true);
                ratingLowToHigh.setChecked(false);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });

        ratingLowToHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceHighToLow.setChecked(false);
                priceLowToHigh.setChecked(false);
                quantityHighToLow.setChecked(false);
                quantityLowToHigh.setChecked(false);
                ratingHighToLow.setChecked(false);
                ratingLowToHigh.setChecked(true);
                fetchFilteredProducts(sQliteHelper, query, recyclerView);
            }
        });
    }

    private void fetchFilteredProducts(SQliteHelper dbHelper, String searchQuery, RecyclerView recyclerView) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Food> filteredFoodList = new ArrayList<>();

        String query = "SELECT * FROM foods WHERE name LIKE ?";

        String orderBy = "";
        if(priceHighToLow.isChecked()){
            orderBy = " ORDER BY price DESC";
        }

        if(priceLowToHigh.isChecked()){
            orderBy = " ORDER BY price ASC";
        }

        if(quantityHighToLow.isChecked()){
            orderBy = " ORDER BY qty DESC";
        }

        if(quantityLowToHigh.isChecked()){
            orderBy = " ORDER BY qty ASC";
        }

        if(ratingHighToLow.isChecked()){
            orderBy = " ORDER BY rating DESC";
        }

        if(ratingLowToHigh.isChecked()){
            orderBy = " ORDER BY rating ASC";
        }

        ArrayList<Food> foodList = new ArrayList<>();
        Cursor cursor = db.rawQuery(query + orderBy, new String[]{"%" + searchQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                Log.i("foodId",cursor.getString(0));
                Log.i("foodName",cursor.getString(1));
                Log.i("foodDescription",cursor.getString(2));
                Log.i("foodPrice",cursor.getString(3));
                Log.i("foodRating",cursor.getString(4));
                Log.i("foodCalories",cursor.getString(5));
                Log.i("foodQty",cursor.getString(6));
                Log.i("foodCategoriesId",cursor.getString(7));
                Food food = new Food(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        Double.parseDouble(cursor.getString(3)),
                        "",
                        cursor.getString(4),
                        cursor.getString(5),
                        Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7))

                );
                filteredFoodList.add(food);
            } while (cursor.moveToNext());

            // Update RecyclerView
            SearchFoodAdapter adapter = new SearchFoodAdapter(filteredFoodList);
            recyclerView.setAdapter(adapter);
        }
        cursor.close();
        db.close();

    }

}

class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.SearchFoodViewHolder>{

    public SearchFoodAdapter(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    class SearchFoodViewHolder extends RecyclerView.ViewHolder {

        public TextView PopularNowTextView1;

        public TextView PopularNowTextView2;

        public ImageView PopularNowImageView;

        public SearchFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            PopularNowTextView1 = itemView.findViewById(R.id.textView25);
            PopularNowTextView2 = itemView.findViewById(R.id.textView26);
            PopularNowImageView = itemView.findViewById(R.id.imageView3);
        }

    }

    public ArrayList<Food> foodList;

    @NonNull
    @Override
    public SearchFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout,parent,false);

        SearchFoodViewHolder foodViewHolder = new SearchFoodViewHolder(view);

        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFoodViewHolder holder, int position) {
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