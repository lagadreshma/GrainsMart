package com.example.grainsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerOrdersAdapter extends RecyclerView.Adapter<SellerOrdersAdapter.SellerOrderViewHolder> {

    private final ArrayList<OrdersModel> ordersList;
    private DatabaseReference customersRef; // DatabaseReference for Firebase

    public SellerOrdersAdapter(ArrayList<OrdersModel> ordersList, DatabaseReference customersRef) {
        this.ordersList = ordersList;
        this.customersRef = customersRef;
    }

    @NonNull
    @Override
    public SellerOrdersAdapter.SellerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_order_single_item, parent, false);
        return new SellerOrdersAdapter.SellerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrdersAdapter.SellerOrderViewHolder holder, int position) {

        OrdersModel order = ordersList.get(position);
        String cid = order.getCid();

        // Set data to the TextViews
        holder.orderId.setText(order.getOid());
        holder.grainName.setText(order.getGname());
        holder.grainTotalPrice.setText("Rs. " + String.valueOf(order.getGtotalprice()));
        holder.orderStatus.setText(order.getOrderstatus());
        holder.paymentStatus.setText(order.getPaymentstatus());
        holder.orderDate.setText(order.getOrderdate());

        // Fetch buyerName from Firebase using cid
        fetchBuyerNameFromFirebase(holder.buyerName, cid);
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

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class SellerOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, buyerName, grainName, grainTotalPrice, orderStatus, paymentStatus, orderDate;

        public SellerOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            buyerName = itemView.findViewById(R.id.buyerName);
            grainName = itemView.findViewById(R.id.grainName);
            grainTotalPrice = itemView.findViewById(R.id.grainTotalPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            orderDate = itemView.findViewById(R.id.orderDate);

        }
    }
}
