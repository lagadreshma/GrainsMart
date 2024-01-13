package com.example.grainsmart;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grainsmart.ui.BuyerAllCategories.BuyerBuySingleItemActivity;

import java.util.ArrayList;

public class BuyerGrainAdapter extends RecyclerView.Adapter<BuyerGrainAdapter.BuyerGrainViewHolder> {

    private ArrayList<GrainsDataModel> buyerGrainList;
    private CustomerSessionManager customerSessionManager;

    public BuyerGrainAdapter(ArrayList<GrainsDataModel> buyerGrainList, CustomerSessionManager sessionManager) {
        this.buyerGrainList = buyerGrainList;
        this.customerSessionManager = sessionManager;
    }


    @NonNull
    @Override
    public BuyerGrainAdapter.BuyerGrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_single_grain_item, parent, false);
        return new BuyerGrainAdapter.BuyerGrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerGrainAdapter.BuyerGrainViewHolder holder, int position) {

        GrainsDataModel grainData = buyerGrainList.get(position);

        // Bind data to views in your item_grain layout
        holder.grainNameTextView.setText(grainData.getGname());
        holder.grainPriceTextView.setText("Rs. " + String.valueOf(grainData.getGprice()));
        holder.grainQuantityTextView.setText("Qty : " + String.valueOf(grainData.getGquantity()) + " Tone");
        holder.grainLocationTextView.setText(grainData.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(grainData.getGimage())
                .into(holder.grainImg);

        holder.buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(customerSessionManager.isLoggedIn()){

                    String grainId = grainData.getGid();
                    String sellerId = grainData.getFid();

                    Log.d("BuyerGrainAdapter", "Buy Now Button Clicked - Grain ID: " + grainId + ", Seller ID: " + sellerId);

                    Intent intent = new Intent(v.getContext(), BuyerBuySingleItemActivity.class);
                    intent.putExtra("gid", grainId);
                    intent.putExtra("fid", sellerId);
                    v.getContext().startActivity(intent);

                }else {

                    Toast.makeText(v.getContext(), "Login Please", Toast.LENGTH_SHORT).show();

                }



            }

        });


    }

    @Override
    public int getItemCount() {
        return buyerGrainList.size();
    }

    public class BuyerGrainViewHolder extends RecyclerView.ViewHolder {

        ImageView grainImg;
        TextView dateTextView, grainNameTextView, grainPriceTextView, grainQuantityTextView, grainLocationTextView;
        Button buyNowBtn;

        public BuyerGrainViewHolder(@NonNull View itemView) {
            super(itemView);

            grainImg = itemView.findViewById(R.id.grainImage);
            grainNameTextView = itemView.findViewById(R.id.grainName);
            grainPriceTextView = itemView.findViewById(R.id.grainPrice);
            grainQuantityTextView = itemView.findViewById(R.id.grainQuantity);
            grainLocationTextView = itemView.findViewById(R.id.location);

            buyNowBtn = itemView.findViewById(R.id.buyBtn);

        }
    }
}
