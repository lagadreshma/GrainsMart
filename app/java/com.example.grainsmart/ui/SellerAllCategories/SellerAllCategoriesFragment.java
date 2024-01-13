package com.example.grainsmart.ui.SellerAllCategories;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.grainsmart.GrainAdapter;
import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerLoginActivity;
import com.example.grainsmart.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerAllCategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private GrainAdapter grainAdapter;
    private ArrayList<GrainsDataModel> grainList;

    // Initialize Firebase Database reference
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_all_categories, container, false);

        recyclerView = view.findViewById(R.id.grainrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        sessionManager = new SessionManager(requireContext());

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(requireContext(), SellerLoginActivity.class);
            startActivity(intent);
        }

        String fid = sessionManager.getKeySellerId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Grains");

        grainList = new ArrayList<>();
        grainAdapter = new GrainAdapter(grainList);
        recyclerView.setAdapter(grainAdapter);

        // Attach a listener to read the data from Firebase
        databaseReference.orderByChild("fid").equalTo(fid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grainList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GrainsDataModel grainData = dataSnapshot.getValue(GrainsDataModel.class);
                    if (grainData != null) {
                        grainList.add(grainData);
                    }
                }
                grainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


        return view;
    }


}