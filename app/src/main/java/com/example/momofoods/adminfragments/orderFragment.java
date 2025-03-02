package com.example.momofoods.adminfragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momofoods.AdminOrderUpdateActivity;
import com.example.momofoods.InvoiceActivity;
import com.example.momofoods.R;
import com.example.momofoods.addOrderActivity;
import com.example.momofoods.model.Invoice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class orderFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.adminOrderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db.collection("invoice").orderBy("datetime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(getContext(),"No orders yet",Toast.LENGTH_SHORT).show();
                            } else {

                                // Use a HashSet to store unique invoice IDs
                                Set<Integer> uniqueInvoiceIds = new HashSet<>();

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    int invoiceId = documentSnapshot.getLong("invoiceNo").intValue();

                                    // Check if invoiceId is already added
                                    if (!uniqueInvoiceIds.contains(invoiceId)) {
                                        uniqueInvoiceIds.add(invoiceId);
                                        int totalOrder = uniqueInvoiceIds.size();

                                        TextView textView = view.findViewById(R.id.textView84);
                                        textView.setText("Total Orders: " + String.valueOf(totalOrder));

                                        String name = documentSnapshot.getString("userName");
                                        String mobile = documentSnapshot.getString("userMobile");
                                        String address = documentSnapshot.getString("address");
                                        double total = documentSnapshot.getDouble("totalPrice");
                                        double fee = documentSnapshot.getDouble("fee");
                                        int invoiceNo = documentSnapshot.getLong("invoiceNo").intValue();
                                        String datetime = documentSnapshot.getString("datetime");
                                        int status = documentSnapshot.getLong("status").intValue();


                                        // Add to invoice list
                                        Invoice invoice = new Invoice(invoiceNo,0,0,"","","",fee,total,address,name,mobile,datetime,status);
                                        invoiceList.add(invoice);
                                    }
                                }

                                // Update RecyclerView
                                OrderAdapter adapter = new OrderAdapter(invoiceList);
                                recyclerView.setAdapter(adapter);



                            }
                        } else {
                            Toast.makeText(getContext(), "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something Went Wrong. Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                })
        ;

        ConstraintLayout constraintLayout = view.findViewById(R.id.adminOrderAddLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext().getApplicationContext(), addOrderActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public OrderAdapter(ArrayList<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView InvoiceNoTextView;
        public TextView InvoiceAmountTextView;
        public TextView InvoiceDateTextView;
        public TextView InvoiceStatusTextView;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            InvoiceNoTextView = itemView.findViewById(R.id.textView88);
            InvoiceAmountTextView = itemView.findViewById(R.id.textView89);
            InvoiceDateTextView = itemView.findViewById(R.id.textView90);
            InvoiceStatusTextView = itemView.findViewById(R.id.textView91);

        }

    }

    public ArrayList<Invoice> invoiceList;

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order, parent, false);

        OrderViewHolder orderViewHolder = new OrderViewHolder(view);

        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.InvoiceNoTextView.setText(String.valueOf(invoiceList.get(position).getInvoiceNo()));
        holder.InvoiceAmountTextView.setText("LKR " + String.valueOf(invoiceList.get(position).getTotalPrice()));
        holder.InvoiceDateTextView.setText(invoiceList.get(position).getDatetime());
        
        if(invoiceList.get(position).getStatus() == 1){
            holder.InvoiceStatusTextView.setText("Preparing");
            holder.InvoiceStatusTextView.setTextColor(Color.parseColor("#EF5350"));
        } else if(invoiceList.get(position).getStatus() == 2){
            holder.InvoiceStatusTextView.setText("On the way");
            holder.InvoiceStatusTextView.setTextColor(Color.parseColor("#FBC02D"));
        } else if(invoiceList.get(position).getStatus() == 3){
            holder.InvoiceStatusTextView.setText("Delivered");
            holder.InvoiceStatusTextView.setTextColor(Color.parseColor("#7CB342"));
        } else {
            holder.InvoiceStatusTextView.setText("Completed");
            holder.InvoiceStatusTextView.setTextColor(Color.parseColor("#B71C1C"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AdminOrderUpdateActivity.class);
                intent.putExtra("name", invoiceList.get(position).getUserName());
                intent.putExtra("mobile", invoiceList.get(position).getUserMobile());
                intent.putExtra("address", invoiceList.get(position).getAddress());
                intent.putExtra("totalPrice", invoiceList.get(position).getTotalPrice());
                intent.putExtra("fee", invoiceList.get(position).getFee());
                intent.putExtra("invoiceNo", invoiceList.get(position).getInvoiceNo());
                view.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.invoiceList.size();
    }
}