package com.example.grainsmart.ui.SellerAllCategories;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerDataModel;
import com.example.grainsmart.SellerLoginActivity;
import com.example.grainsmart.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateGrainItemActivity extends AppCompatActivity {

    private TextInputEditText editGName, editGVariety, editGPrice, editGQuantity, editLocation, editUpi;
    private ImageView grainImage;

    Uri grainImageUri;
    Uri scannerImageUri;
    private AppCompatButton btnUpdateGrain;

    private SessionManager sessionManager;

    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    String gid, fid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grain_item);

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("Grains");

        editGName = findViewById(R.id.editGName);
        editGVariety = findViewById(R.id.editGVariety);
        editGPrice = findViewById(R.id.editGPrice);
        editGQuantity = findViewById(R.id.editGQuantity);
        editLocation = findViewById(R.id.editLocation);
        grainImage = findViewById(R.id.grainImage);
        editUpi = findViewById(R.id.upiId);
        btnUpdateGrain = findViewById(R.id.btnUpdateGrain);

        sessionManager = new SessionManager(getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), SellerLoginActivity.class);
            startActivity(intent);
        }

        //   Select Grain Image ---------------------------------
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){

                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            grainImageUri = data.getData();
                            grainImage.setImageURI(grainImageUri);
                        }else{
                            Toast.makeText(getApplicationContext(), "No Grain Image Selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

//      ImageView Onclick-------------

        grainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);

            }
        });


        getGrainDetails();

        btnUpdateGrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGrainData();
                onBackPressed();
            }

        });


    }

    private void getGrainDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            gid = intent.getStringExtra("gid");
            fid = intent.getStringExtra("fid");
        }

        Log.d("UpdateGrainItemActivity", "Received Data - Grain ID: " + gid + ", Seller ID: " + fid);

        String sellerId = sessionManager.getKeySellerId();

        if (fid.equals(sellerId)) {

            if (gid != null) {

                DatabaseReference grainRef = databaseReference.child(gid);
                grainRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            GrainsDataModel grains = snapshot.getValue(GrainsDataModel.class);
                            if (grains != null) {
                                // Populate UI with existing data
                                editGName.setText(grains.getGname());
                                editGVariety.setText(grains.getGvariety());
                                editGPrice.setText(String.valueOf(grains.getGprice()));
                                editGQuantity.setText(String.valueOf(grains.getGquantity()));
                                editLocation.setText(grains.getLocation());
                                editUpi.setText(grains.getUpiId());

                                String imageGrainUrl = grains.getGimage();
                                Picasso.get().load(imageGrainUrl).into(grainImage);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to get grains data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("UpdateGrainItemActivity", "gid is null");
                            Toast.makeText(getApplicationContext(), "Grains data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.toException().printStackTrace();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Item ID not provided", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if item ID is not provided
            }
        }
    }


    private void updateGrainData() {
        String newGname = editGName.getText().toString();
        String newGvariety = editGVariety.getText().toString();
        double newGprice = Double.parseDouble(editGPrice.getText().toString());
        int newGquantity = Integer.parseInt(editGQuantity.getText().toString());
        String newLocation = editLocation.getText().toString();
        String newUpiId = editUpi.getText().toString();

        // Perform the update in the database
        DatabaseReference reference = databaseReference.child(gid);

        reference.child("gname").setValue(newGname);
        reference.child("gvariety").setValue(newGvariety);
        reference.child("gprice").setValue(newGprice);
        reference.child("gquantity").setValue(newGquantity);
        reference.child("location").setValue(newLocation);
        reference.child("upiId").setValue(newUpiId);

        Toast.makeText(getApplicationContext(), "Grain Details Updated.", Toast.LENGTH_SHORT).show();
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