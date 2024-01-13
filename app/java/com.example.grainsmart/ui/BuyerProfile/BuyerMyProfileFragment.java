package com.example.grainsmart.ui.BuyerProfile;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grainsmart.CustomerDataModel;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.R;
import com.example.grainsmart.ui.SignIn.BuyerSignInFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerMyProfileFragment extends Fragment {

    private DatabaseReference databaseReference;
    private EditText editName, editEmail, editPassword, editAddress, editMobileno;
    private CircleImageView imageView;
    AppCompatButton updateBtn;
    private View view;
    CustomerSessionManager sessionManager;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_my_profile, container, false);

        FirebaseApp.initializeApp(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editName = view.findViewById(R.id.nameEditText);
        editEmail = view.findViewById(R.id.emailEditText);
        editPassword = view.findViewById(R.id.passwordEditText);
        editAddress = view.findViewById(R.id.addressEditText);
        editMobileno = view.findViewById(R.id.mobileEditText);
        imageView = view.findViewById(R.id.uploadImage);
        updateBtn = view.findViewById(R.id.updateBtn);

        sessionManager = new CustomerSessionManager(requireContext());

        if (!sessionManager.isLoggedIn()) {

            Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
            replaceFragment(new BuyerSignInFragment(), R.id.nav_signIn);

        }

        retrieveSellerData();

        return view;

    }

    private void retrieveSellerData() {

        if(sessionManager.isLoggedIn()){
            String buyerId = sessionManager.getKeyBuyerId();

            if(buyerId != null){
                databaseReference.child("Customers").child(buyerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            CustomerDataModel buyer = snapshot.getValue(CustomerDataModel.class);

                            if (buyer != null) {

                                editName.setText(buyer.getCname());
                                editEmail.setText(buyer.getCemail());
                                editPassword.setText(buyer.getCpassword());
                                editAddress.setText(buyer.getCaddress());
                                editMobileno.setText(buyer.getCcontact());

                                String imageUrl = buyer.getCprofilepic();
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

            }
        }



    }

    private void updateProfileData(String newName, String newEmail, String newPassword, String newAddress, String newMobileNo) {
        String buyerId = sessionManager.getKeyBuyerId();

        if (buyerId != null) {
            DatabaseReference buyerRef = databaseReference.child("Customers").child(buyerId);

            buyerRef.child("cname").setValue(newName);
            buyerRef.child("cemail").setValue(newEmail);
            buyerRef.child("cpassword").setValue(newPassword);
            buyerRef.child("caddress").setValue(newAddress);
            buyerRef.child("cmobileno").setValue(newMobileNo);
        } else {
            Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
            replaceFragment(new BuyerSignInFragment(), R.id.nav_signIn);
        }

    }

    private void replaceFragment(Fragment fragment, int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_buyer_main);
        navController.navigate(destinationId);
    }

}