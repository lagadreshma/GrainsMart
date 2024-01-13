package com.example.grainsmart.ui.BuyerAllCategories;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.grainsmart.BuyerGrainAdapter;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuyerAllCategoriesFragment extends Fragment {

    RecyclerView grainRecycler;
    BuyerGrainAdapter buyerGrainAdapter;
    ArrayList<GrainsDataModel> buyerGrainList;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;

    Button buyNowBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_all_categories, container, false);

        buyerGrainList = new ArrayList<>();
        grainRecycler = view.findViewById(R.id.grainRV);

        grainRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        CustomerSessionManager sessionManager = new CustomerSessionManager(requireContext());
        buyerGrainAdapter = new BuyerGrainAdapter(buyerGrainList, sessionManager);
        grainRecycler.setAdapter(buyerGrainAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Grains");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                buyerGrainList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                    GrainsDataModel grainsDataModel = itemSnapshot.getValue(GrainsDataModel.class);
                    buyerGrainList.add(grainsDataModel);

                }
                buyerGrainAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                dialog.dismiss();

            }
        });

        return view;

    }

}