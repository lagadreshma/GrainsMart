package com.example.grainsmart.ui.Dashboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.grainsmart.OrdersModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerCompleteOrdersAdapter;
import com.example.grainsmart.SellerOrdersAdapter;
import com.example.grainsmart.SellerPendingOrdersAdapter;
import com.example.grainsmart.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private TextView totalOrders, totalGrains, totalPendingOrders, totalCompleteOrders;
    private DatabaseReference ordersDatabaseReference;
    private DatabaseReference grainsDatabaseReference;
    private DatabaseReference customerDatabaseReference;

    SessionManager sessionManager;

    private RecyclerView pendingOrderRecycler;
    private RecyclerView completeOrderRecycler;
    private SellerPendingOrdersAdapter sellerPendingOrderAdapter;
    private SellerCompleteOrdersAdapter sellerCompleteOrdersAdapter;
    private ArrayList<OrdersModel> sellerPendingOrderList;
    private ArrayList<OrdersModel> sellerCompleteOrderList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        sessionManager = new SessionManager(getContext());
        String sellerId = sessionManager.getKeySellerId();

        customerDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        ordersDatabaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        grainsDatabaseReference = FirebaseDatabase.getInstance().getReference("Grains");

        totalOrders = view.findViewById(R.id.totalOrders);
        totalGrains = view.findViewById(R.id.totalGrains);
        totalPendingOrders = view.findViewById(R.id.totalPendingOrders);
        totalCompleteOrders = view.findViewById(R.id.totalCompleteOrders);

        fetchTotalGrains();
        fetchTotalOrders();
        // Fetch number of pending orders for the specific fid
        fetchTotalPendingOrders();
        // Fetch number of complete orders for the specific fid
        fetchTotalCompleteOrders();


        // fetch all pending orders
        sellerPendingOrderList = new ArrayList<>();
        pendingOrderRecycler = view.findViewById(R.id.pendingOrdersRV);
        pendingOrderRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        sellerPendingOrderAdapter = new SellerPendingOrdersAdapter(sellerPendingOrderList, customerDatabaseReference);
        pendingOrderRecycler.setAdapter(sellerPendingOrderAdapter);

        // Attach a listener to read the data from Firebase
        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerPendingOrderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersModel orderData = dataSnapshot.getValue(OrdersModel.class);
                    if (orderData != null && ("Pending").equals(orderData.getOrderstatus())) {
                        sellerPendingOrderList.add(orderData);
                    }
                }
                sellerPendingOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


        // fetch all complete orders
        sellerCompleteOrderList = new ArrayList<>();
        completeOrderRecycler = view.findViewById(R.id.completeOrdersRV);
        completeOrderRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        sellerCompleteOrdersAdapter = new SellerCompleteOrdersAdapter(sellerCompleteOrderList, customerDatabaseReference);
        completeOrderRecycler.setAdapter(sellerCompleteOrdersAdapter);  // <-- Corrected

// Attach a listener to read the data from Firebase
        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerCompleteOrderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersModel orderData = dataSnapshot.getValue(OrdersModel.class);
                    if (orderData != null && ("Complete").equals(orderData.getOrderstatus())) {
                        sellerCompleteOrderList.add(orderData);
                    }
                }
                sellerCompleteOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        return view;
    }

    private void fetchTotalGrains() {

        String sellerId = sessionManager.getKeySellerId();

        grainsDatabaseReference.orderByChild("fid").equalTo(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalgrains = snapshot.getChildrenCount();
                totalGrains.setText(String.valueOf(totalgrains));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void fetchTotalOrders() {

        String sellerId = sessionManager.getKeySellerId();

        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalOrder = snapshot.getChildrenCount();
                Log.d("Total Orders", "Total Orders Count: " + totalOrder);
                totalOrders.setText(String.valueOf(totalOrder));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void fetchTotalPendingOrders() {
        String sellerId = sessionManager.getKeySellerId();

        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long pendingOrders = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrdersModel order = orderSnapshot.getValue(OrdersModel.class);
                    if (order != null && "Pending".equals(order.getOrderstatus())) {
                        pendingOrders++;
                    }
                }

                totalPendingOrders.setText(String.valueOf(pendingOrders));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void fetchTotalCompleteOrders() {

        String sellerId = sessionManager.getKeySellerId();
        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int completeOrders = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrdersModel order = orderSnapshot.getValue(OrdersModel.class);
                    if (order != null && "Complete".equals(order.getOrderstatus())) {
                        completeOrders++;
                    }
                }

                totalCompleteOrders.setText(String.valueOf(completeOrders));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

    }


}