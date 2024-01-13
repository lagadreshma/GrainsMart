package com.example.grainsmart.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grainsmart.BuyerGrainAdapter;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.example.grainsmart.databinding.ActivityBuyerMainBinding;
import com.example.grainsmart.databinding.FragmentHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    CustomerSessionManager sessionManager;

    SearchView searchView;
    RecyclerView grainRecycler;
    BuyerGrainAdapter buyerGrainAdapter;
    ArrayList<GrainsDataModel> buyerGrainList;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buyerGrainList = new ArrayList<>();
        grainRecycler = view.findViewById(R.id.grainRV);
        searchView = view.findViewById(R.id.search);

        grainRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        sessionManager = new CustomerSessionManager(getContext());

        buyerGrainAdapter = new BuyerGrainAdapter(buyerGrainList, sessionManager);
        grainRecycler.setAdapter(buyerGrainAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Grains");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                buyerGrainList.clear();

                for(DataSnapshot itemSnapshot : snapshot.getChildren()){

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