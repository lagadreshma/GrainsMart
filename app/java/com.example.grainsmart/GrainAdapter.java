package com.example.grainsmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grainsmart.ui.SellerAllCategories.UpdateGrainItemActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GrainAdapter extends RecyclerView.Adapter<GrainAdapter.GrainViewHolder> {

    private final ArrayList<GrainsDataModel> grainList;

    public GrainAdapter(ArrayList<GrainsDataModel> grainList) {
        this.grainList = grainList;
    }

    @NonNull
    @Override
    public GrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grain_single_item, parent, false);
        return new GrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrainViewHolder holder, int position) {
        GrainsDataModel grainData = grainList.get(position);

        // Bind data to views in your item_grain layout
        holder.dateTextView.setText(grainData.getGrainAddedDate());
        holder.grainNameTextView.setText(grainData.getGname());
        holder.grainVarietyTextView.setText(grainData.getGvariety());
        holder.grainPriceTextView.setText("Rs. " + String.valueOf(grainData.getGprice()));
        holder.grainQuantityTextView.setText(String.valueOf(grainData.getGquantity()) + " Tone");
        holder.grainLocationTextView.setText(grainData.getLocation());

        Glide.with(holder.itemView.getContext())
                .load(grainData.getGimage())
                .into(holder.grainImg);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Item.")
                        .setMessage("Are you sure want to delete?")
                        .setIcon(R.drawable.baseline_delete_24)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String gid = grainList.get(holder.getAdapterPosition()).getGid();
                                String img = grainList.get(holder.getAdapterPosition()).getGimage();

                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grains");
                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                StorageReference storageReference = storage.getReferenceFromUrl(img);
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        reference.child(gid).removeValue();
                                        Toast.makeText(v.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }
                                });

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });

                builder.show();

            }
        });


        // Update item

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String grainId = grainList.get(holder.getAdapterPosition()).getGid();
                String sellerId = grainList.get(holder.getAdapterPosition()).getFid();

                Log.d("GrainAdapter", "Update Button Clicked - Grain ID: " + grainId + ", Seller ID: " + sellerId);

                Intent intent = new Intent(v.getContext(), UpdateGrainItemActivity.class);

                intent.putExtra("gid", grainId);
                intent.putExtra("fid", sellerId);
                v.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return grainList.size();
    }

    public class GrainViewHolder extends RecyclerView.ViewHolder {

        CircleImageView grainImg;
        TextView dateTextView, grainNameTextView, grainVarietyTextView, grainPriceTextView, grainQuantityTextView, grainLocationTextView;
        AppCompatButton updateBtn, deleteBtn;

        public GrainViewHolder(@NonNull View itemView) {
            super(itemView);

            grainImg = itemView.findViewById(R.id.grainImage);
            dateTextView = itemView.findViewById(R.id.date);
            grainNameTextView = itemView.findViewById(R.id.grainName);
            grainVarietyTextView = itemView.findViewById(R.id.grainVariety);
            grainPriceTextView = itemView.findViewById(R.id.grainPrice);
            grainQuantityTextView = itemView.findViewById(R.id.grainQuantity);
            grainLocationTextView = itemView.findViewById(R.id.grainLocation);

            updateBtn = itemView.findViewById(R.id.updateBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }


    }

}
