package com.example.grainsmart.ui.SellerOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.OrdersModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerLoginActivity;
import com.example.grainsmart.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateOrderStatusActivity extends AppCompatActivity {

    private TextView orderId, buyerName, grainName, grainTotalPrice, orderDate, grainPrice, grainQuantity;
    private TextInputEditText orderStatus, paymentStatus;

    private AppCompatButton backBtn, updateBtn;

    private DatabaseReference orderDatabaseReference;
    private DatabaseReference buyerDatabaseReference;
    private DatabaseReference grainDatabaseReference;
    private SessionManager sessionManager;

    private String oid, cid, fid, gid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        orderId = findViewById(R.id.orderId);
        buyerName = findViewById(R.id.buyerName);
        grainName = findViewById(R.id.grainName);
        grainTotalPrice = findViewById(R.id.grainTotalPrice);
        grainPrice = findViewById(R.id.grainPrice);
        grainQuantity = findViewById(R.id.grainQuantity);
        orderStatus = findViewById(R.id.orderStatus);
        paymentStatus = findViewById(R.id.paymentStatus);
        orderDate = findViewById(R.id.orderDate);

        backBtn = findViewById(R.id.backBtn);
        updateBtn = findViewById(R.id.updateOrderStatusBtn);

        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        buyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        grainDatabaseReference = FirebaseDatabase.getInstance().getReference("Grains");

        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), SellerLoginActivity.class);
            startActivity(intent);
        }

        getOrderDetails();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateOrderStatus();
                onBackPressed();

            }
        });


    }

    private void getOrderDetails(){

        Intent intent = getIntent();
        if(intent != null){
            oid = intent.getStringExtra("oid");
            cid = intent.getStringExtra("cid");
            fid = intent.getStringExtra("fid");
            gid = intent.getStringExtra("gid");
        }

        Log.d("UpdateGrainItemActivity", "Received Data - Order ID: " + oid + ", Buyer ID: " + cid);

        String sellerId = sessionManager.getKeySellerId();

        if(fid.equals(sellerId)){

            if(oid != null){

                DatabaseReference orderRef = orderDatabaseReference.child(oid);
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            OrdersModel order = snapshot.getValue(OrdersModel.class);
                            if(order != null){
                                orderId.setText(order.getOid());
                                grainName.setText(order.getGname());
                                grainPrice.setText("Rs. " + String.valueOf(order.getGprice()) + "/Kg");
                                grainQuantity.setText(String.valueOf(order.getGquantity()) + " Kg");
                                grainTotalPrice.setText(String.valueOf("Rs. " + order.getGtotalprice()));
                                orderDate.setText(order.getOrderdate());
                                orderStatus.setText(order.getOrderstatus());
                                paymentStatus.setText(order.getPaymentstatus());

                                // Fetch buyerName from Firebase using cid
                                fetchBuyerNameFromFirebase(buyerName, cid);

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed to get Order Details ", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Log.d("UpdateOrderStatusActivity", "oid is null");
                            Toast.makeText(getApplicationContext(), "Order data does not exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.toException().printStackTrace();

                    }
                });

            }else {
                Toast.makeText(getApplicationContext(), "Item ID not provided", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if item ID is not provided
            }

        }


    }
    
    private void fetchBuyerNameFromFirebase(final TextView buyerNameTextView, String cid){
        
        buyerDatabaseReference.child(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                if(snapshot.exists()){
                    String buyerName = snapshot.child("cname").getValue(String.class);
                    buyerNameTextView.setText(buyerName);
                }
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
    }

    private void updateOrderStatus(){

        String newOrderStatus = orderStatus.getText().toString();
        String newPaymentStatus = paymentStatus.getText().toString();

        DatabaseReference reference = orderDatabaseReference.child(oid);

        reference.child("orderstatus").setValue(newOrderStatus);
        reference.child("paymentstatus").setValue(newPaymentStatus);

        if ("Complete".equals(newOrderStatus)) {
            // Order status is being updated to "Complete," update grain quantity
            updateGrainQuantity(gid);
        }

        Toast.makeText(getApplicationContext(), "Order Status Updated.", Toast.LENGTH_SHORT).show();


    }

    private void updateGrainQuantity(String gid) {
        // Fetch the current grain quantity
        grainDatabaseReference.child(gid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GrainsDataModel grains = snapshot.getValue(GrainsDataModel.class);
                    if (grains != null) {
                        int currentQuantity = grains.getGquantity(); // Assuming 'quantity' is the field representing grain quantity
                        int orderedQuantity = Integer.parseInt(grainQuantity.getText().toString()); // Extract the ordered quantity from the UI
                        int updatedQuantity = currentQuantity - orderedQuantity;

                        // Update the grain quantity in the database
                        grainDatabaseReference.child(gid).child("gquantity").setValue(updatedQuantity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
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

}