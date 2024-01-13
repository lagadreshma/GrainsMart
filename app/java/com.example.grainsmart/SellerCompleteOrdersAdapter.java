package com.example.grainsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerCompleteOrdersAdapter extends RecyclerView.Adapter<SellerCompleteOrdersAdapter.SellerCompleteOrderViewHolder> {

    private final ArrayList<OrdersModel> ordersList;
    private DatabaseReference customersRef; // DatabaseReference for Firebase

    public SellerCompleteOrdersAdapter(ArrayList<OrdersModel> ordersList, DatabaseReference customersRef) {
        this.ordersList = ordersList;
        this.customersRef = customersRef;
    }

    @NonNull
    @Override
    public SellerCompleteOrdersAdapter.SellerCompleteOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_complete_orders_single_item, parent, false);
        return new SellerCompleteOrdersAdapter.SellerCompleteOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerCompleteOrdersAdapter.SellerCompleteOrderViewHolder holder, int position) {

        OrdersModel order = ordersList.get(position);
        String cid = order.getCid();

        holder.orderId.setText(order.getOid());
        holder.grainName.setText(order.getGname());

        // Fetch buyerName from Firebase using cid
        fetchBuyerNameFromFirebase(holder.customerName, cid);

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class SellerCompleteOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, customerName, grainName;

        public SellerCompleteOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            customerName = itemView.findViewById(R.id.customerName);
            grainName = itemView.findViewById(R.id.grainName);

        }
    }

    private void fetchBuyerNameFromFirebase(final TextView buyerNameTextView, String cid) {
        customersRef.child(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming "name" is the field in Customers node containing the buyerName
                    String buyerName = dataSnapshot.child("cname").getValue(String.class);
                    buyerNameTextView.setText(buyerName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

}
