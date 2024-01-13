package com.example.grainsmart;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grainsmart.ui.SellerOrders.UpdateOrderStatusActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerPendingOrdersAdapter extends RecyclerView.Adapter<SellerPendingOrdersAdapter.SellerPendingOrderViewHolder>{

    private final ArrayList<OrdersModel> ordersList;
    private DatabaseReference customersRef; // DatabaseReference for Firebase

    public SellerPendingOrdersAdapter(ArrayList<OrdersModel> ordersList, DatabaseReference customersRef) {
        this.ordersList = ordersList;
        this.customersRef = customersRef;
    }

    @NonNull
    @Override
    public SellerPendingOrdersAdapter.SellerPendingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_pending_orders_single_item, parent, false);
        return new SellerPendingOrdersAdapter.SellerPendingOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerPendingOrdersAdapter.SellerPendingOrderViewHolder holder, int position) {

        OrdersModel order = ordersList.get(position);
        String cid = order.getCid();

        holder.orderId.setText(order.getOid());
        holder.grainName.setText(order.getGname());

        // Fetch buyerName from Firebase using cid
        fetchBuyerNameFromFirebase(holder.customerName, cid);


        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String orderId = ordersList.get(holder.getAdapterPosition()).getOid();
                String sellerId = ordersList.get(holder.getAdapterPosition()).getFid();
                String grainId = ordersList.get(holder.getAdapterPosition()).getGid();
                String customerId = ordersList.get(holder.getAdapterPosition()).getCid();

                Log.d("SellerPendingOrdersAdapter", "Update Button Clicked - Order ID: " + orderId + ", Grain ID: " + grainId + ", Seller ID: " + sellerId);

                Intent intent = new Intent(v.getContext(), UpdateOrderStatusActivity.class);
                intent.putExtra("oid", orderId);
                intent.putExtra("gid", grainId);
                intent.putExtra("fid", sellerId);
                intent.putExtra("cid", customerId);
                v.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class SellerPendingOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, customerName, grainName;
        AppCompatButton updateBtn;

        public SellerPendingOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            customerName = itemView.findViewById(R.id.customerName);
            grainName = itemView.findViewById(R.id.grainName);

            updateBtn = itemView.findViewById(R.id.updateOrder);

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
