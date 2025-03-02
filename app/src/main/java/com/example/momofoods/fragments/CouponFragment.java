package com.example.momofoods.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momofoods.InvoiceActivity;
import com.example.momofoods.R;
import com.example.momofoods.model.Food;
import com.example.momofoods.model.Invoice;
import com.example.momofoods.model.SQliteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CouponFragment extends Fragment {

    public String userMobile = "";

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
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.historyRecyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Invoice> invoiceList = new ArrayList<>();

        db.collection("invoice").whereEqualTo("userMobile",userMobile).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(getContext(),"Purchase History is Empty",Toast.LENGTH_SHORT).show();
                            } else {

                                // Use a HashSet to store unique invoice IDs
                                Set<Integer> uniqueInvoiceIds = new HashSet<>();

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    int invoiceId = documentSnapshot.getLong("invoiceNo").intValue();

                                    // Check if invoiceId is already added
                                    if (!uniqueInvoiceIds.contains(invoiceId)) {
                                        uniqueInvoiceIds.add(invoiceId);

                                        String name = documentSnapshot.getString("userName");
                                        String mobile = documentSnapshot.getString("userMobile");
                                        String address = documentSnapshot.getString("address");
                                        double total = documentSnapshot.getDouble("totalPrice");
                                        double fee = documentSnapshot.getDouble("fee");
                                        int invoiceNo = documentSnapshot.getLong("invoiceNo").intValue();
                                        String datetime = documentSnapshot.getString("datetime");


                                        // Add to invoice list
                                        Invoice invoice = new Invoice(invoiceNo,0,0,"","","",fee,total,address,name,mobile,datetime,1);
                                        invoiceList.add(invoice);
                                    }
                                }

                                // Update RecyclerView
                                InvoiceAdapter adapter = new InvoiceAdapter(invoiceList);
                                recyclerView.setAdapter(adapter);


                            }
                        } else {
                            Toast.makeText(getContext(), "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }
}

class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    public InvoiceAdapter(ArrayList<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        public TextView InvoiceNoTextView;

        public TextView InvoiceAmountTextView;;
        public TextView InvoiceDateTextView;;

        public Layout CartItemLayout;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            InvoiceNoTextView = itemView.findViewById(R.id.textView38);
            InvoiceAmountTextView = itemView.findViewById(R.id.textView71);
            InvoiceDateTextView = itemView.findViewById(R.id.textView83);

        }

    }

    public ArrayList<Invoice> invoiceList;

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_history_item_layout, parent, false);

        InvoiceViewHolder cartViewHolder = new InvoiceViewHolder(view);

        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        holder.InvoiceNoTextView.setText("Invoice: " + String.valueOf(invoiceList.get(position).getInvoiceNo()));
        holder.InvoiceAmountTextView.setText("LKR " + String.valueOf(invoiceList.get(position).getTotalPrice()));
        holder.InvoiceDateTextView.setText(invoiceList.get(position).getDatetime());


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), InvoiceActivity.class);
            intent.putExtra("name", invoiceList.get(position).getUserName());
            intent.putExtra("mobile", invoiceList.get(position).getUserMobile());
            intent.putExtra("address", invoiceList.get(position).getAddress());
            intent.putExtra("totalPrice", invoiceList.get(position).getTotalPrice());
            intent.putExtra("fee", invoiceList.get(position).getFee());
            intent.putExtra("invoiceNo", invoiceList.get(position).getInvoiceNo());
            view.getContext().startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return this.invoiceList.size();
    }
}