package com.example.grainsmart.ui.SignIn;

import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.R;
import com.example.grainsmart.databinding.ActivityBuyerMainBinding;
import com.example.grainsmart.ui.BuyerProfile.BuyerMyProfileFragment;
import com.example.grainsmart.ui.SignUp.BuyerSignUpFragment;
import com.example.grainsmart.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerSignInFragment extends Fragment {

    TextView registerNow;
    TextInputEditText loginEmail, loginPassword;
    AppCompatButton loginButton;

    private ActivityBuyerMainBinding binding;
    CustomerSessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_sign_in, container, false);

        binding = ActivityBuyerMainBinding.inflate(getLayoutInflater());

        loginEmail = view.findViewById(R.id.emailEditText);
        loginPassword = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginBtn);
        registerNow = view.findViewById(R.id.registerNow);

        sessionManager = new CustomerSessionManager(requireContext());

        if(sessionManager.isLoggedIn()){
            Toast.makeText(getContext(), "You Already SignIn", Toast.LENGTH_SHORT).show();
            replaceFragment(new BuyerMyProfileFragment(), R.id.nav_myProfile);
        }

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new BuyerSignUpFragment(), R.id.nav_signUp);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateEmail() | !validatePassword()){



                }else{

                    checkUser();

                    // setting nav_header username and email

                    DrawerLayout drawer = binding.drawerLayout;
                    NavigationView navigationView = binding.navView;

                    View header = navigationView.getHeaderView(0);
                    TextView txtUserName  = (TextView) header.findViewById(R.id.navUserName);
                    TextView txtUserEmailId = (TextView)header.findViewById(R.id.navUserEmail);
                    CircleImageView profileImage = header.findViewById(R.id.navProfileImage);

                    txtUserName.setText(sessionManager.getUsername());
                    txtUserEmailId.setText(sessionManager.getEmail());

                    String profileImageUrl = sessionManager.getFprofilepic();

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.my_profile);
                    }

                }

            }
        });

        return view;

    }

    public Boolean validateEmail(){

        String val = loginEmail.getText().toString();
        if(val.isEmpty()){
            loginEmail.setError("Email Cannot be Empty.");
            return false;
        }else{
            loginEmail.setError(null);
            return true;
        }

    }

    public Boolean validatePassword(){

        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Password Cannot be Empty.");
            return false;
        }else{
            loginPassword.setError(null);
            return true;
        }

    }

    public void checkUser() {
        String buyerEmail = loginEmail.getText().toString().trim();
        String buyerPass = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers");
        Query checkUserDatabase = reference.orderByChild("cemail").equalTo(buyerEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = childSnapshot.child("cpassword").getValue(String.class);
                        String buyerNameFromDB = childSnapshot.child("cname").getValue(String.class);
                        String buyerProfilePic = childSnapshot.child("cprofilepic").getValue(String.class);
                        String buyerIdFromDB = childSnapshot.child("cid").getValue(String.class); // Retrieve sellerId

                        if (passwordFromDB != null && passwordFromDB.equals(buyerPass)) {
                            // Password matches, login successful
                            loginEmail.setError(null);
                            loginPassword.setError(null);

                            sessionManager.createLoginSession(buyerIdFromDB, buyerNameFromDB, buyerEmail, buyerProfilePic);

                            // Consider using Firebase Authentication for secure user authentication
                            // For this example, let's assume the login is successful and move to the next screen
                            Toast.makeText(requireContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                            replaceFragment(new HomeFragment(), R.id.nav_home);
                            return;
                        }
                    }
                    // Password didn't match
                    loginEmail.setError(null);
                    loginPassword.setError("Invalid Credentials");
                    loginPassword.requestFocus();


                } else {
                    // User not found in the database
                    loginEmail.setError("User Does not exist");
                    loginEmail.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private void replaceFragment(Fragment fragment, int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_buyer_main);
        navController.navigate(destinationId);
    }


}