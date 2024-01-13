package com.example.grainsmart;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SellerRegisterActivity extends AppCompatActivity {

    TextView loginNow;

    TextInputEditText nameEditText, emailEditText, passwordEditText, phoneEditText, addressEditText;
    ImageView uploadImage;
    Button registerBtn;
    String imageUrl;
    Uri uri;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneEditText = findViewById(R.id.mobileEditText);
        addressEditText = findViewById(R.id.addressEditText);
        uploadImage = findViewById(R.id.uploadImage);

        registerBtn = findViewById(R.id.registerBtn);
        loginNow = findViewById(R.id.loginNow);

//   Select Image ---------------------------------
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){

                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        }else{
                            Toast.makeText(SellerRegisterActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

//      ImageView Onclick

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);

            }
        });


//   Register Button onClick---------------------------------

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Farmers");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//   Call saveData() method-------------------------

                if(validateInput()){

                    saveData();

                }

            }
        });


//   Login Now TextView onClick-------------------------------
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(SellerRegisterActivity.this, SellerLoginActivity.class);
                startActivity(loginIntent);

            }
        });


    }

    private boolean validateInput() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Enter a valid phone number");
            return false;
        }

        if (address.isEmpty()) {
            nameEditText.setError("address cannot be empty");
            return false;
        }

        return true;
    }


//  saveData() for Farmers Registration----------------------------
    public void saveData(){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("SellerProfile Pictures").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(SellerRegisterActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadData();

                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });


    }


//   uploadData() method for uploading Farmers (sellers) data on firebase realtime database
    public void uploadData() {

        String sname = nameEditText.getText().toString().trim();
        String semail = emailEditText.getText().toString().trim();
        String spassword = passwordEditText.getText().toString().trim();
        String smobile = phoneEditText.getText().toString().trim();
        String saddress = addressEditText.getText().toString().trim();

        String sid = databaseReference.push().getKey();

        SellerDataModel sellerDataModel = new SellerDataModel(sid,sname, semail, spassword, smobile, saddress, imageUrl);
        databaseReference.child(sid).setValue(sellerDataModel).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SellerRegisterActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();

                            Intent activity = new Intent(SellerRegisterActivity.this, SellerLoginActivity.class);
                            startActivity(activity);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellerRegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // Log the error in detail for debugging
                    }
                });

    }


}