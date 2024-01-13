package com.example.grainsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SellerLoginActivity extends AppCompatActivity {

    TextView registerNow;

    TextInputEditText loginEmail, loginPassword;
    Button loginButton;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        loginEmail = findViewById(R.id.emailEditText);
        loginPassword = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginBtn);
        registerNow = findViewById(R.id.registerNow);

        sessionManager = new SessionManager(getApplicationContext());

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(SellerLoginActivity.this, SellerRegisterActivity.class);
                startActivity(registerIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateEmail() | !validatePassword()){



                }else{

                    checkUser();

                }

            }
        });


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
        String sellerEmail = loginEmail.getText().toString().trim();
        String sellerPass = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Farmers");
        Query checkUserDatabase = reference.orderByChild("femail").equalTo(sellerEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = childSnapshot.child("fpassword").getValue(String.class);
                        String sellerNameFromDB = childSnapshot.child("fname").getValue(String.class);
                        String sellerProfilePic = childSnapshot.child("fprofilepic").getValue(String.class);
                        String sellerIdFromDB = childSnapshot.child("fid").getValue(String.class); // Retrieve sellerId

                        if (passwordFromDB != null && passwordFromDB.equals(sellerPass)) {
                            // Password matches, login successful
                            loginEmail.setError(null);
                            loginPassword.setError(null);

                            sessionManager.createLoginSession(sellerIdFromDB, sellerNameFromDB, sellerEmail, sellerProfilePic);

                            // Consider using Firebase Authentication for secure user authentication
                            // For this example, let's assume the login is successful and move to the next screen
                            Intent intent = new Intent(SellerLoginActivity.this, SellerMainActivity.class); // Replace NextActivity with your desired activity
                            startActivity(intent);
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

}