package com.example.momofoods.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.momofoods.CheckOutActivity;
import com.example.momofoods.R;
import com.example.momofoods.SingleProductViewActivity;
import com.example.momofoods.model.Cart;
import com.example.momofoods.model.Food;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CartFragment extends Fragment {

    public String userMobile = "";
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public AtomicInteger TotalPrice = new AtomicInteger();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQliteHelper sQliteHelper = new SQliteHelper(getContext(), "Momofoods.db",null,1);

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.CartRecyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        ArrayList<Food> foodList = new ArrayList<>();
//        AtomicInteger TotalPrice = new AtomicInteger();
//
//        db.collection("cart").whereEqualTo("userMobile",userMobile).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            if(task.getResult().isEmpty()){
//                                Toast.makeText(getContext(),"Cart is Empty",Toast.LENGTH_SHORT).show();
//                            } else {
//
//                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                                    String id = documentSnapshot.getString("productId");
//                                    String name = documentSnapshot.getString("name");
//                                    String description = documentSnapshot.getString("description");
//                                    int price = documentSnapshot.getLong("price").intValue();
//                                    String imageUrl = documentSnapshot.getString("imageUrl");
//                                    String rating = documentSnapshot.getString("rating");
//                                    String calories = documentSnapshot.getString("calories");
//                                    int qty = documentSnapshot.getLong("quantity").intValue();
//                                    int category_id = 1;
//
//                                    // Add to RecyclerView List
//                                    Food food = new Food(id, name, description, Double.parseDouble(String.valueOf(price)), imageUrl, rating, calories, qty, category_id);
//                                    foodList.add(food);
//
//                                    TotalPrice.addAndGet(price * qty);
//
//                                }
//
//                                // Update RecyclerView
//                                CartAdapter adapter = new CartAdapter(foodList);
//                                recyclerView.setAdapter(adapter);
//
//                                TextView totalPrice = view.findViewById(R.id.textView40);
//                                totalPrice.setText("LKR "+TotalPrice.toString()+".00");
//
//
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        loadCartItems(recyclerView,view);

        Button checkout = view.findViewById(R.id.button7);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sent total price to checkout activity
                Intent intent = new Intent(getContext(), CheckOutActivity.class);
                intent.putExtra("totalPrice",TotalPrice.toString());
                startActivity(intent);
            }
        });

        return view;
    }

    public void loadCartItems(RecyclerView recyclerView, View view){
        ArrayList<Cart> foodList = new ArrayList<>();

        db.collection("cart").whereEqualTo("userMobile",userMobile).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(view.getContext(),"Cart is Empty",Toast.LENGTH_SHORT).show();
                            } else {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String itemId = documentSnapshot.getId();
                                    String id = documentSnapshot.getString("productId");
                                    String name = documentSnapshot.getString("name");
                                    String description = documentSnapshot.getString("description");
                                    int price = documentSnapshot.getLong("price").intValue();
                                    String imageUrl = documentSnapshot.getString("imageUrl");
                                    String rating = documentSnapshot.getString("rating");
                                    String calories = documentSnapshot.getString("calories");
                                    int qty = documentSnapshot.getLong("quantity").intValue();
                                    int category_id = 1;

                                    // Add to RecyclerView List
                                    Cart cart = new Cart(itemId,id,name,description,price,imageUrl,rating,calories,qty,userMobile);
                                    foodList.add(cart);

                                    TotalPrice.addAndGet(price * qty);

                                }

                                // Update RecyclerView
                                CartAdapter adapter = new CartAdapter(foodList);
                                recyclerView.setAdapter(adapter);

                                TextView totalPrice = view.findViewById(R.id.textView40);
                                totalPrice.setText("LKR "+TotalPrice.toString()+".00");


                            }
                        } else {
                            Toast.makeText(getContext(), "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    public CartAdapter(ArrayList<Cart> foodList) {
        this.foodList = foodList;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        public TextView CartItemNamesTextView;

        public TextView CartItemPriceTextView;;

        public ImageView CartItemImageView;

        public TextView CartItemQuantityText;

        public ImageButton DeleteItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            CartItemNamesTextView = itemView.findViewById(R.id.textView32);
            CartItemPriceTextView = itemView.findViewById(R.id.textView34);
            CartItemImageView = itemView.findViewById(R.id.imageView7);
            CartItemQuantityText = itemView.findViewById(R.id.textView39);
            DeleteItem = itemView.findViewById(R.id.imageButton4);

        }

    }

    public ArrayList<Cart> foodList;

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);

        CartViewHolder cartViewHolder = new CartViewHolder(view);

        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.CartItemNamesTextView.setText(foodList.get(position).getName());
        holder.CartItemPriceTextView.setText(String.valueOf(foodList.get(position).getPrice()));
        holder.CartItemQuantityText.setText("Qty: "+String.valueOf(foodList.get(position).getQuantity()));
        Glide.with(holder.itemView.getContext()).load("https://limegreen-cattle-220394.hostingersite.com/uploads/"+foodList.get(position).getProductId()+".jpg").into(holder.CartItemImageView);
        CartFragment cartFragment = new CartFragment();
        holder.DeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("cart").document(foodList.get(position).getCartItemId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(view.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                                foodList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, foodList.size());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Item Delete Failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                ;
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SingleProductViewActivity.class);
            intent.putExtra("foodId",foodList.get(position).getProductId());
            intent.putExtra("foodName",foodList.get(position).getName());
            intent.putExtra("foodDescription",foodList.get(position).getDescription());
            intent.putExtra("foodPrice",String.valueOf(foodList.get(position).getPrice()));
            view.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return this.foodList.size();
    }
}