package com.example.momofoods.adminfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.momofoods.AddProductActivity;
import com.example.momofoods.AdminChangeProductActivity;
import com.example.momofoods.R;
import com.example.momofoods.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class productFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.addProductRecyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayout8);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddProductActivity.class);
                startActivity(intent);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Food> foodList = new ArrayList<>();
        db.collection("foods").orderBy("datetime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        TextView textView86 = view.findViewById(R.id.textView86);
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(view.getContext(), "No foods Available", Toast.LENGTH_SHORT).show();
                                textView86.setText("Total Products: 0");
                            } else {
                                textView86.setText("Total Products: "+ task.getResult().size());

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

                                    // Add to RecyclerView List
                                    Food food = new Food(id, name, description, Double.parseDouble(price), imageUrl, rating, calories, quantity, category_id);
                                    foodList.add(food);

                                }

                                // Update RecyclerView
                                AdminFoodAdapter adapter = new AdminFoodAdapter(foodList);
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

class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder>{

    public AdminFoodAdapter(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    class AdminFoodViewHolder extends RecyclerView.ViewHolder {

        public TextView AdminProductName;
        public TextView AdminProductQuantity;
        public TextView AdminProductPrice;
        public ImageView AdminProductImageView;
        public AdminFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            AdminProductName = itemView.findViewById(R.id.textView150);
            AdminProductQuantity = itemView.findViewById(R.id.textView152);
            AdminProductPrice = itemView.findViewById(R.id.textView151);
            AdminProductImageView = itemView.findViewById(R.id.imageView18);
        }

    }

    public ArrayList<Food> foodList;

    @NonNull
    @Override
    public AdminFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_layout,parent,false);

        AdminFoodViewHolder foodViewHolder = new AdminFoodViewHolder(view);

        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFoodViewHolder holder, int position) {
        holder.AdminProductName.setText(foodList.get(position).getName());
        holder.AdminProductPrice.setText("LKR "+String.valueOf(foodList.get(position).getPrice()));
        holder.AdminProductQuantity.setText("Qty: "+ String.valueOf(foodList.get(position).getQty()));
        Glide.with(holder.itemView.getContext()).load("https://limegreen-cattle-220394.hostingersite.com/uploads/"+foodList.get(position).getId()+".jpg").into(holder.AdminProductImageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AdminChangeProductActivity.class);
            intent.putExtra("foodId",foodList.get(position).getId());
            intent.putExtra("foodName",foodList.get(position).getName());
            intent.putExtra("foodDescription",foodList.get(position).getDescription());
            intent.putExtra("foodPrice",String.valueOf(foodList.get(position).getPrice()));
            intent.putExtra("foodRating",foodList.get(position).getRating());
            intent.putExtra("foodCalories",foodList.get(position).getCalories());
            intent.putExtra("foodImageUrl",foodList.get(position).getImageUrl());
            intent.putExtra("foodQty",String.valueOf(foodList.get(position).getQty()));
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return this.foodList.size();
    }
}