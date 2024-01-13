package com.example.grainsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    private DatabaseReference databaseReference;
    private EditText editName, editEmail, editPassword, editAddress, editMobileno;
    private CircleImageView imageView;
    Button updateBtn;
    private View view;
    private SessionManager sessionManager;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        FirebaseApp.initializeApp(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editName = view.findViewById(R.id.nameEditText);
        editEmail = view.findViewById(R.id.emailEditText);
        editPassword = view.findViewById(R.id.passwordEditText);
        editAddress = view.findViewById(R.id.addressEditText);
        editMobileno = view.findViewById(R.id.mobileEditText);
        imageView = view.findViewById(R.id.uploadImage);
        updateBtn = view.findViewById(R.id.updateBtn);

        sessionManager = new SessionManager(requireContext());

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(requireContext(), SellerLoginActivity.class);
            startActivity(intent);
        }

        retrieveSellerData();

        return view;

    }

    private void retrieveSellerData() {

        String sellerId = sessionManager.getKeySellerId();

        if(sellerId != null){
        databaseReference.child("Farmers").child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    SellerDataModel seller = snapshot.getValue(SellerDataModel.class);

                    if (seller != null) {

                        editName.setText(seller.getFname());
                        editEmail.setText(seller.getFemail());
                        editPassword.setText(seller.getFpassword());
                        editAddress.setText(seller.getFaddress());
                        editMobileno.setText(seller.getFmobileno());

                        String imageUrl = seller.getFprofilepic();
                        Picasso.get().load(imageUrl).into(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Launch image selection activity
                                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                                photoPicker.setType("image/*");
                                startActivityForResult(photoPicker, PICK_IMAGE_REQUEST);
                            }
                        });


                        updateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String newName = editName.getText().toString();
                                String newEmail = editEmail.getText().toString();
                                String newPassword = editPassword.getText().toString();
                                String newAddress = editAddress.getText().toString();
                                String newMobileNo = editMobileno.getText().toString();

                                // Perform the update in the database
                                updateProfileData(newName, newEmail, newPassword, newAddress, newMobileNo);

                                Toast.makeText(requireContext(), "Profile Updated.", Toast.LENGTH_SHORT).show();

                            }
                        });


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {

                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.toException().printStackTrace();
            }
        });


    }else

    {
        Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
    }

  }

    private void updateProfileData(String newName, String newEmail, String newPassword, String newAddress, String newMobileNo) {
        String sellerId = sessionManager.getKeySellerId();

        if (sellerId != null) {
            DatabaseReference sellerRef = databaseReference.child("Farmers").child(sellerId);

            sellerRef.child("fname").setValue(newName);
            sellerRef.child("femail").setValue(newEmail);
            sellerRef.child("fpassword").setValue(newPassword);
            sellerRef.child("faddress").setValue(newAddress);
            sellerRef.child("fmobileno").setValue(newMobileNo);
        } else {
            Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
        }

    }



}