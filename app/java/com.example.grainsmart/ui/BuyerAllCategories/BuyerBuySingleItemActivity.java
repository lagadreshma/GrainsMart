package com.example.grainsmart.ui.BuyerAllCategories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerDataModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BuyerBuySingleItemActivity extends AppCompatActivity {

    private DatabaseReference grainDatabaseReference;
    private DatabaseReference sellerDatabaseReference;
    CustomerSessionManager customerSessionManager;
    AppCompatButton backBtn, checkoutBtn;

    private TextView postedDateTextView, grainNameTextView, grainVarietyTextView,
            grainPriceTextView, grainQuantityTextView, grainLocationTextView,
            sellerNameTextView, sellerAddressTextView, sellerMobileNoTextView;
    private RadioButton cashOnDeliveryRadioButton, onlinePaymentRadioButton;
    private TextInputEditText grainQuantity;
    private String gid, fid;

    private ImageView grainImageView, sellerImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_buy_single_item);

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerSessionManager = new CustomerSessionManager(getApplicationContext());

        if (!customerSessionManager.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "Login Please", Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase
        grainDatabaseReference = FirebaseDatabase.getInstance().getReference("Grains");
        sellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Farmers");

        // Initialize Views
        postedDateTextView = findViewById(R.id.postedDate);
        grainNameTextView = findViewById(R.id.grainName);
        grainVarietyTextView = findViewById(R.id.grainVariety);
        grainPriceTextView = findViewById(R.id.grainPrice);
        grainQuantityTextView = findViewById(R.id.grainQuantity);
        grainLocationTextView = findViewById(R.id.grainLocation);
        sellerNameTextView = findViewById(R.id.sellerName);
        sellerAddressTextView = findViewById(R.id.sellerAddress);
        sellerMobileNoTextView = findViewById(R.id.sellerMobileNo);
        grainImageView = findViewById(R.id.grainImage);
        sellerImageView = findViewById(R.id.sellerImage);

        grainQuantity = findViewById(R.id.quantityEditText);

        cashOnDeliveryRadioButton = findViewById(R.id.cashOnDelivery);
        onlinePaymentRadioButton = findViewById(R.id.onlinePayment);

        backBtn = findViewById(R.id.backBtn);
        checkoutBtn = findViewById(R.id.checkoutBtn);

        // Get gid and fid from Intent
        gid = getIntent().getStringExtra("gid");
        fid = getIntent().getStringExtra("fid");

        // Query Firebase with gid and fid
        DatabaseReference grainRef = grainDatabaseReference.child(gid);
        DatabaseReference sellerRef = sellerDatabaseReference.child(fid);


        // Retrieve and populate grain details
        grainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GrainsDataModel grainData = dataSnapshot.getValue(GrainsDataModel.class);

                    postedDateTextView.setText(grainData.getGrainAddedDate());
                    grainNameTextView.setText(grainData.getGname());
                    grainVarietyTextView.setText(grainData.getGvariety());
                    grainPriceTextView.setText("Rs. " + String.valueOf(grainData.getGprice()) + " /Kg");
                    grainQuantityTextView.setText("Qty : " + String.valueOf(grainData.getGquantity()) + " Tone");
                    grainLocationTextView.setText(grainData.getLocation());

                    Glide.with(BuyerBuySingleItemActivity.this)
                            .load(grainData.getGimage())
                            .into(grainImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseDebug", "Error retrieving grain data: " + databaseError.getMessage());
            }
        });


        // Retrieve and populate seller details
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SellerDataModel sellerData = dataSnapshot.getValue(SellerDataModel.class);

                    sellerNameTextView.setText(sellerData.getFname());
                    sellerAddressTextView.setText(sellerData.getFaddress());
                    sellerMobileNoTextView.setText(sellerData.getFmobileno());

                    Glide.with(BuyerBuySingleItemActivity.this)
                            .load(sellerData.getFprofilepic())
                            .into(sellerImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseDebug", "Error retrieving Seller data: " + databaseError.getMessage());
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the TextInputEditText
                String gQuantityString = grainQuantity.getText().toString();

                                // Check which RadioButton is selected
                if (cashOnDeliveryRadioButton.isChecked()) {
                    // Cash on delivery is selected

                    // Validate if the quantity is not empty
                    int gQuantity = 0; // default value
                    if (gQuantityString != null && !gQuantityString.isEmpty()) {
                        try {
                            gQuantity = Integer.parseInt(gQuantityString);
                        } catch (NumberFormatException e) {
                            // Handle the exception (e.g., log it or show an error message)
                            e.printStackTrace();
                        }

                        if(gQuantity >= 10){
                            // Intent to BuyerCheckoutActivity
                            Intent grainIntent = new Intent(getApplicationContext(), CashOnDeliveryActivity.class);
                            grainIntent.putExtra("gid", gid);
                            grainIntent.putExtra("fid", fid);
                            grainIntent.putExtra("gQuantity", gQuantity);

                            startActivity(grainIntent);
                        }else{

                            Toast.makeText(BuyerBuySingleItemActivity.this, "Enter Quantity Greater than 10", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        // Show an error message if the quantity is empty
                        Toast.makeText(BuyerBuySingleItemActivity.this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                    }

                } else if (onlinePaymentRadioButton.isChecked()) {
                    // Online payment is selected

                    // Validate if the quantity is not empty
                    int gQuantity = 0; // default value
                    if (gQuantityString != null && !gQuantityString.isEmpty()) {
                        try {
                            gQuantity = Integer.parseInt(gQuantityString);
                        } catch (NumberFormatException e) {
                            // Handle the exception (e.g., log it or show an error message)
                            e.printStackTrace();
                        }

                        if(gQuantity >= 10){
                            // Intent to BuyerCheckoutActivity
                            Intent grainIntent = new Intent(getApplicationContext(), OnlinePaymentActivity.class);
                            grainIntent.putExtra("gid", gid);
                            grainIntent.putExtra("fid", fid);
                            grainIntent.putExtra("gQuantity", gQuantity);

                            startActivity(grainIntent);
                        }else{

                            Toast.makeText(BuyerBuySingleItemActivity.this, "Enter Quantity Greater than 10", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        // Show an error message if the quantity is empty
                        Toast.makeText(BuyerBuySingleItemActivity.this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // No RadioButton is selected
                    Toast.makeText(getApplicationContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    public void onBackPressed() {
        // Check if any fragments are in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the last fragment from the back stack
            getSupportFragmentManager().popBackStack();
        } else {
            // If no fragments in the back stack, proceed with the default back button behavior
            super.onBackPressed();
        }
    }
    private void replaceFragment(Fragment fragment, int destinationId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(destinationId, fragment);
        transaction.addToBackStack(null); // Add the fragment to the back stack
        transaction.commit();
    }


}