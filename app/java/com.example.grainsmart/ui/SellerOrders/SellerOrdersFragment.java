package com.example.grainsmart.ui.SellerOrders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grainsmart.OrdersModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerOrdersAdapter;
import com.example.grainsmart.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerOrdersFragment extends Fragment {

    private RecyclerView orderRecycler;
    private SellerOrdersAdapter sellerOrderAdapter;
    private ArrayList<OrdersModel> sellerOrderList;

    private SessionManager sessionManager;
    private DatabaseReference customerDatabaseReference;
    private DatabaseReference ordersDatabaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_orders, container, false);

        customerDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        ordersDatabaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        sessionManager = new SessionManager(getContext());
        String sellerId = sessionManager.getKeySellerId();

        sellerOrderList = new ArrayList<>();
        orderRecycler = view.findViewById(R.id.sellerOrderRV);
        orderRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        sellerOrderAdapter = new SellerOrdersAdapter(sellerOrderList, customerDatabaseReference);
        orderRecycler.setAdapter(sellerOrderAdapter);

        // Attach a listener to read the data from Firebase
        ordersDatabaseReference.orderByChild("fid").equalTo(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerOrderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersModel orderData = dataSnapshot.getValue(OrdersModel.class);
                    if (orderData != null) {
                        sellerOrderList.add(orderData);
                    }
                }
                sellerOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


        return view;

    }

}