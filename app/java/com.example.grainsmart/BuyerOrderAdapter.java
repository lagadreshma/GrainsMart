package com.example.grainsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BuyerOrderAdapter extends RecyclerView.Adapter<BuyerOrderAdapter.OrderViewHolder> {

    private final ArrayList<OrdersModel> ordersList;

    public BuyerOrderAdapter(ArrayList<OrdersModel> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public BuyerOrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_order_single_item, parent, false);
        return new BuyerOrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerOrderAdapter.OrderViewHolder holder, int position) {

        OrdersModel order = ordersList.get(position);

        // Set data to the TextViews
        holder.orderId.setText(order.getOid());
        holder.buyerId.setText(order.getCid());
        holder.grainName.setText(order.getGname());
        holder.grainQuantity.setText("Rs. " + String.valueOf(order.getGquantity()));
        holder.grainPrice.setText("Rs. " + String.valueOf(order.getGprice()));
        holder.grainTotalPrice.setText("Rs. " + String.valueOf(order.getGtotalprice()));
        holder.orderStatus.setText(order.getOrderstatus());
        holder.paymentStatus.setText(order.getPaymentstatus());
        holder.orderDate.setText(order.getOrderdate());

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{

        TextView orderId, buyerId, grainName, grainQuantity, grainPrice,
                grainTotalPrice, orderStatus, paymentStatus, orderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize TextViews and CardView
            orderId = itemView.findViewById(R.id.orderId);
            buyerId = itemView.findViewById(R.id.buyerId);
            grainName = itemView.findViewById(R.id.grainName);
            grainQuantity = itemView.findViewById(R.id.grainQuantity);
            grainPrice = itemView.findViewById(R.id.grainPrice);
            grainTotalPrice = itemView.findViewById(R.id.grainTotalPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            orderDate = itemView.findViewById(R.id.orderDate);

        }
    }
}
