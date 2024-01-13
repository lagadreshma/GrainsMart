package com.example.grainsmart.ui.BuyerOrders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.grainsmart.BuyerOrderAdapter;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.OrdersModel;
import com.example.grainsmart.R;
import com.example.grainsmart.ui.SignIn.BuyerSignInFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuyerMyOrdersFragment extends Fragment {

    private RecyclerView orderRecycler;
    private BuyerOrderAdapter buyerOrderAdapter;
    private ArrayList<OrdersModel> buyerOrderList;

    private CustomerSessionManager sessionManager;
    private DatabaseReference databaseReference;
    ValueEventListener eventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_my_orders, container, false);

        sessionManager = new CustomerSessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {

            Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
            replaceFragment(new BuyerSignInFragment(), R.id.nav_signIn);

        }

        String cid = sessionManager.getKeyBuyerId();

        buyerOrderList = new ArrayList<>();
        orderRecycler = view.findViewById(R.id.orderrv);
        orderRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        buyerOrderAdapter = new BuyerOrderAdapter(buyerOrderList);
        orderRecycler.setAdapter(buyerOrderAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Attach a listener to read the data from Firebase
        databaseReference.orderByChild("cid").equalTo(cid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buyerOrderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersModel orderData = dataSnapshot.getValue(OrdersModel.class);
                    if (orderData != null) {
                        buyerOrderList.add(orderData);
                    }
                }
                buyerOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        return view;

    }

    private void replaceFragment(Fragment fragment, int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_buyer_main);
        navController.navigate(destinationId);
    }

}