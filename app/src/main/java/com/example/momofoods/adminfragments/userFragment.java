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
import android.widget.TextView;
import android.widget.Toast;

import com.example.momofoods.AddUserActivity;
import com.example.momofoods.AdminChangeEmployeeActivity;
import com.example.momofoods.R;
import com.example.momofoods.model.Employee;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class userFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.adminUserRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayout11);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext().getApplicationContext(), AddUserActivity.class);
                startActivity(intent);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Employee> userList = new ArrayList<>();
        db.collection("employees").orderBy("datetime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        TextView textView87 = view.findViewById(R.id.textView87);
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(view.getContext(), "No foods Available", Toast.LENGTH_SHORT).show();
                                textView87.setText("Total Employees: 0");
                            } else {
                                textView87.setText("Total Employees: "+ task.getResult().size());

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String id = documentSnapshot.getId();
                                    String name = documentSnapshot.getString("name");
                                    String mobile = documentSnapshot.getString("phone");
                                    String email = documentSnapshot.getString("email");
                                    String userId = documentSnapshot.getString("id");
                                    String password = documentSnapshot.getString("password");
                                    int role = documentSnapshot.getLong("role").intValue();
                                    String dateime = documentSnapshot.getString("datetime");

                                    // Add to RecyclerView List
                                    Employee employees = new Employee(id,name,mobile,email,userId,password,role,dateime);
                                    userList.add(employees);

                                }

                                // Update RecyclerView
                                UserAdapter adapter = new UserAdapter(userList);
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

class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    public UserAdapter(ArrayList<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView EmployeeName;
        public TextView EmployeeMobile;
        public TextView EmplyeeRole;
        public TextView EmployeeDateTime;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            EmployeeName = itemView.findViewById(R.id.textView103);
            EmployeeMobile = itemView.findViewById(R.id.textView125);
            EmplyeeRole = itemView.findViewById(R.id.textView126);
            EmployeeDateTime = itemView.findViewById(R.id.textView134);
        }

    }

    public ArrayList<Employee> employeeList;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_layout,parent,false);

        UserViewHolder userViewHolder = new UserViewHolder(view);

        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.EmployeeName.setText(employeeList.get(position).getName());
        holder.EmployeeMobile.setText(employeeList.get(position).getPhone());

        if(employeeList.get(position).getRole() == 2){
            holder.EmplyeeRole.setText("Waiter");
        } else if(employeeList.get(position).getRole() == 3){
            holder.EmplyeeRole.setText("Chef");
        } else if(employeeList.get(position).getRole() == 4){
            holder.EmplyeeRole.setText("Cashier");
        } else if(employeeList.get(position).getRole() == 5){
            holder.EmplyeeRole.setText("Delivery");
        } else {
            holder.EmplyeeRole.setText("None");
        }
        holder.EmployeeDateTime.setText(employeeList.get(position).getDatetime());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AdminChangeEmployeeActivity.class);
            intent.putExtra("employeeId",employeeList.get(position).getId());
            intent.putExtra("employeeName",employeeList.get(position).getName());
            intent.putExtra("employeeMobile",employeeList.get(position).getPhone());
            intent.putExtra("employeeEmail",employeeList.get(position).getEmail());
            intent.putExtra("employeeNic",employeeList.get(position).getNic());
            intent.putExtra("employeePassword",employeeList.get(position).getPassword());
            intent.putExtra("employeeRole",String.valueOf(employeeList.get(position).getRole()));
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return this.employeeList.size();
    }
}