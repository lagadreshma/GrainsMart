package com.example.grainsmart.ui.BuyerAllCategories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.grainsmart.BuyerMainActivity;
import com.example.grainsmart.CustomerDataModel;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.OrdersModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CashOnDeliveryActivity extends AppCompatActivity {

    private TextView grainNameTextView, grainVarietyTextView, grainQuantityTextView,
            grainPriceTextView, grainTotalPriceTextView, grainSellerNameTextView,
            grainSellerMobileNoTextView, customerNameTextView, customerAddressTextView,
            customerMobileNoTextView;
    private AppCompatButton placeOrderButton;

    private CustomerSessionManager sessionManager;
    DatabaseReference orderDatabaseReference;
    DatabaseReference grainDatabaseReference, sellerDatabaseReference, customerDatabaseReference;
    String gid, cid, fid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_on_delivery);

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new CustomerSessionManager(getApplicationContext());

        // Initialize Views
        grainNameTextView = findViewById(R.id.grainName);
        grainVarietyTextView = findViewById(R.id.grainVariety);
        grainQuantityTextView = findViewById(R.id.grainQuantity);
        grainPriceTextView = findViewById(R.id.grainPrice);
        grainTotalPriceTextView = findViewById(R.id.grainTotalPrice);
        grainSellerNameTextView = findViewById(R.id.grainSellerName);
        grainSellerMobileNoTextView = findViewById(R.id.grainSellerMobileNo);
        customerNameTextView = findViewById(R.id.customerName);
        customerAddressTextView = findViewById(R.id.customerAddress);
        customerMobileNoTextView = findViewById(R.id.customerMobileNo);

        placeOrderButton = findViewById(R.id.placeOrderButton);

        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Get gid and fid from Intent
        gid = getIntent().getStringExtra("gid");
        fid = getIntent().getStringExtra("fid");
        int gQuantity = getIntent().getIntExtra("gQuantity", 10);

        cid = sessionManager.getKeyBuyerId();

        // Assume you have the necessary references to Firebase Database
        grainDatabaseReference = FirebaseDatabase.getInstance().getReference("Grains").child(gid);
        sellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Farmers").child(fid);
        customerDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(cid);

        // Fetch data from Firebase and update UI
        fetchDataFromFirebase(gQuantity);

        // Set a click listener for the Place Order button
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCashOnDeliveryOperation(gQuantity);

            }
        });

    }

    private void fetchDataFromFirebase(int gQuantity) {

        // Fetch data for Grain
        grainDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GrainsDataModel grainData = dataSnapshot.getValue(GrainsDataModel.class);
                    updateGrainUI(grainData, gQuantity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDebug", "Error fetching grain data: " + databaseError.getMessage());
            }
        });

        // Fetch data for Seller
        sellerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SellerDataModel sellerData = dataSnapshot.getValue(SellerDataModel.class);
                    updateSellerUI(sellerData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDebug", "Error fetching seller data: " + databaseError.getMessage());
            }
        });

        // Fetch data for Customer
        customerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CustomerDataModel customerData = dataSnapshot.getValue(CustomerDataModel.class);
                    updateCustomerUI(customerData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDebug", "Error fetching customer data: " + databaseError.getMessage());
            }
        });
    }

    private void updateGrainUI(GrainsDataModel grainData, int gQuantity) {

        Double gprice = grainData.getGprice();
        Double grainTotalPrice = gprice * gQuantity;

        grainNameTextView.setText(grainData.getGname());
        grainVarietyTextView.setText(grainData.getGvariety());
        grainQuantityTextView.setText(gQuantity + " Kg");
        grainPriceTextView.setText(" Rs. " + String.valueOf(grainData.getGprice()) + " /Kg");
        grainTotalPriceTextView.setText(" Rs. " + String.valueOf(grainTotalPrice));

    }

    private void updateSellerUI(SellerDataModel sellerData) {
        grainSellerNameTextView.setText(sellerData.getFname());
        grainSellerMobileNoTextView.setText(sellerData.getFmobileno());
    }

    private void updateCustomerUI(CustomerDataModel customerData) {
        customerNameTextView.setText(customerData.getCname());
        customerAddressTextView.setText(customerData.getCaddress());
        customerMobileNoTextView.setText(customerData.getCcontact());
    }

    private void performCashOnDeliveryOperation(int gQuantity) {


        grainDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GrainsDataModel grainData = dataSnapshot.getValue(GrainsDataModel.class);

                    // Assuming you have the necessary details for constructing the OrdersModel
                    String gname = grainData.getGname();
                    Double gprice = grainData.getGprice();
                    Double gtotalprice = gprice * gQuantity;

                    // Construct OrdersModel
                    String oid = orderDatabaseReference.push().getKey();
                    String orderDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String orderStatus = "Pending"; // You may set an initial status
                    String paymentStatus = "Pending"; // You may set an initial status

                    OrdersModel ordersModel = new OrdersModel(oid, gid, cid, fid, gname,
                            gQuantity, gprice, gtotalprice, orderStatus, paymentStatus, orderDate);

                    // Insert data into the Firebase database Orders table
                    orderDatabaseReference.child(oid).setValue(ordersModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Order Confirmed.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), BuyerMainActivity.class);
                                        intent.putExtra("oid", oid);
                                        startActivity(intent);

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace(); // Log the error in detail for debugging
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDebug", "Error fetching grain data: " + databaseError.getMessage());
            }
        });


    }

}