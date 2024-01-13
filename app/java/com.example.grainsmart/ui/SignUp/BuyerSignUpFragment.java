package com.example.grainsmart.ui.SignUp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grainsmart.CustomerDataModel;
import com.example.grainsmart.CustomerSessionManager;
import com.example.grainsmart.R;
import com.example.grainsmart.ui.BuyerProfile.BuyerMyProfileFragment;
import com.example.grainsmart.ui.SignIn.BuyerSignInFragment;
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

public class BuyerSignUpFragment extends Fragment {

    TextView loginNow;

    TextInputEditText nameEditText, emailEditText, passwordEditText, phoneEditText, addressEditText;
    ImageView uploadImage;
    AppCompatButton registerBtn;
    String imageUrl;
    Uri uri;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    CustomerSessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_sign_up, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        phoneEditText = view.findViewById(R.id.mobileEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        uploadImage = view.findViewById(R.id.uploadImage);

        registerBtn = view.findViewById(R.id.registerBtn);
        loginNow = view.findViewById(R.id.loginNow);

        sessionManager = new CustomerSessionManager(requireContext());

        if(sessionManager.isLoggedIn()){
            Toast.makeText(getContext(), "You Already SignUp", Toast.LENGTH_SHORT).show();
            replaceFragment(new BuyerMyProfileFragment(), R.id.nav_myProfile);
        }

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
                            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
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
        databaseReference = firebaseDatabase.getReference("Customers");

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

                replaceFragment(new BuyerSignInFragment(), R.id.nav_signIn);

            }
        });

        return view;

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

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Customer ProfilePics").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

        String cname = nameEditText.getText().toString().trim();
        String cemail = emailEditText.getText().toString().trim();
        String cpassword = passwordEditText.getText().toString().trim();
        String cmobile = phoneEditText.getText().toString().trim();
        String caddress = addressEditText.getText().toString().trim();

        String cid = databaseReference.push().getKey();

        CustomerDataModel customerDataModel = new CustomerDataModel(cid,cname, cemail, cpassword, cmobile, caddress, imageUrl);
        databaseReference.child(cid).setValue(customerDataModel).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Registration Successful.", Toast.LENGTH_SHORT).show();

                            replaceFragment(new BuyerSignInFragment(), R.id.nav_signIn);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // Log the error in detail for debugging
                    }
                });

    }

    private void replaceFragment(Fragment fragment, int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_buyer_main);
        navController.navigate(destinationId);
    }

}