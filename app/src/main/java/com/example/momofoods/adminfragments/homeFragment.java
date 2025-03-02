package com.example.momofoods.adminfragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.momofoods.R;
import com.example.momofoods.model.Invoice;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class homeFragment extends Fragment {

    private LineChart lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        lineChart = view.findViewById(R.id.lineChart);

        db.collection("foods").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && task.getResult() !=null){
                                    int totalProducts = task.getResult().size();
                                    TextView productCountView = view.findViewById(R.id.textView77);
                                    productCountView.setText(String.valueOf(totalProducts));

                                } else {
                                    Log.e("Firestore", "Error getting documents: ", task.getException());
                                }
                            }
                        });

        db.collection("users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && task.getResult() !=null){
                                    int totalUsers = task.getResult().size();
                                    TextView userCountView = view.findViewById(R.id.textView82);
                                    userCountView.setText(String.valueOf(totalUsers));
                                } else  {
                                    Log.e("Firestore", "Error getting documents: ", task.getException());
                                }
                            }
                        });


        db.collection("invoice")
                .orderBy("datetime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            Log.e("Firestore", "Task failed or no results");
                            return;
                        }

                        Set<Integer> uniqueInvoiceIds = new HashSet<>();
                        List<Entry> dataPoints = new ArrayList<>();
                        AtomicInteger totalSales = new AtomicInteger();

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            // Get invoice number safely
                            Long invoiceLong = documentSnapshot.getLong("invoiceNo");
                            if (invoiceLong == null) continue;

                            int invoiceId = invoiceLong.intValue();
                            if (!uniqueInvoiceIds.add(invoiceId)) continue;

                            // Get total price safely
                            Double total = documentSnapshot.getDouble("totalPrice");
                            if (total == null) continue;

                            // Get and parse datetime
                            String datetime = documentSnapshot.getString("datetime");
                            if (datetime == null) continue;

                            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
                            Matcher matcher = pattern.matcher(datetime);

                            if (matcher.find()) {
                                String dateStr = matcher.group();

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date = sdf.parse(dateStr);
                                    if (date != null) {
                                        long timestamp = date.getTime() / 1000;

                                        // Add entry to chart data
                                        dataPoints.add(new Entry(timestamp, total.floatValue()));

                                        // Log for debugging
                                        Log.d("Firestore", "Date: " + timestamp);
                                        Log.d("Firestore", "Total: " + total.floatValue());
                                    }
                                } catch (ParseException e) {
                                    Log.e("Firestore", "Date parsing error: " + e.getMessage());
                                }
                            } else {
                                Log.e("Firestore", "Invalid date format: " + datetime);
                            }

                            // Add to total sales
                            totalSales.addAndGet(total.intValue());
                        }

                        Log.d("Firestore", "Total Sales: " + totalSales.get());

                        // Update chart
                        updateChart(dataPoints, totalSales.get());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting documents: ", e);
                    }
                });

        return view;
    }

    private void updateChart(List<Entry> dataPoints, int totalSales) {
        if (dataPoints.isEmpty()) return;

        // Create a LineDataSet
        LineDataSet lineDataSet = new LineDataSet(dataPoints, "Total Sales");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Set data to LineChart
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Customize X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Refresh the chart
        lineChart.invalidate();

        TextView totalSalesView = getView().findViewById(R.id.textView80);
        totalSalesView.setText("LKR " + String.valueOf(totalSales));
    }
}