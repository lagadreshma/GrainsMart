package com.example.grainsmart.ui.AddGrains;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grainsmart.GrainsDataModel;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerLoginActivity;
import com.example.grainsmart.SessionManager;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SellerAddGrainFragment extends Fragment {

    private TextInputEditText editGName, editGVariety, editGPrice, editGQuantity, editLocation, editupi;
    private ImageView grainImage;

    Uri grainImageUri;
    Uri scannerImageUri;
    private AppCompatButton btnAddGrain;

    private SessionManager sessionManager;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_add_grain, container, false);

        // if user not logged in
        sessionManager = new SessionManager(requireContext());

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(requireContext(), SellerLoginActivity.class);
            startActivity(intent);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Grains");

        editGName = view.findViewById(R.id.editGName);
        editGVariety = view.findViewById(R.id.editGVariety);
        editGPrice = view.findViewById(R.id.editGPrice);
        editGQuantity = view.findViewById(R.id.editGQuantity);
        editLocation = view.findViewById(R.id.editLocation);
        grainImage = view.findViewById(R.id.grainImage);
        editupi = view.findViewById(R.id.upiId);
        btnAddGrain = view.findViewById(R.id.btnAddGrain);

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
                            Toast.makeText(requireContext(), "No Grain Image Selected", Toast.LENGTH_SHORT).show();
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


//        AddGrain Button onclick-------------
        btnAddGrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInput()){

                    saveData();
                }

            }
        });

        return view;
    }


    public void saveData() {
        // Assuming you have grainImageUri and scannerImageUri as member variables

        if (grainImageUri == null) {
            // Handle the case where one of the image URIs is null
            Toast.makeText(requireContext(), "Please select grain image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference grainStorageReference = FirebaseStorage.getInstance().getReference()
                .child("GrainImages").child(grainImageUri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Upload grainImage
        grainStorageReference.putFile(grainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri grainImageUrl = uriTask.getResult();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                // Handle failure for grainImage
            }
        });
    }

    //    All filled Validation
    private boolean validateInput() {
        String grainName = editGName.getText().toString().trim();
        String grainVariety = editGVariety.getText().toString().trim();
        String grainPriceStr = editGPrice.getText().toString().trim();
        String grainQuantityStr = editGQuantity.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        // Check if grain name is empty
        if (grainName.isEmpty()) {
            editGName.setError("Grain name is required");
            return false;
        }

        // Check if grain variety is empty
        if (grainVariety.isEmpty()) {
            editGVariety.setError("Grain variety is required");
            return false;
        }

        // Check if grain price is empty
        if (grainPriceStr.isEmpty()) {
            editGPrice.setError("Grain price is required");
            return false;
        }

        // Check if grain quantity is empty
        if (grainQuantityStr.isEmpty()) {
            editGQuantity.setError("Grain quantity is required");
            return false;
        }

        // Convert grain price to double
        double grainPrice;
        try {
            grainPrice = Double.parseDouble(grainPriceStr);
        } catch (NumberFormatException e) {
            editGPrice.setError("Invalid grain price");
            return false;
        }

        // Convert grain quantity to int
        int grainQuantity;
        try {
            grainQuantity = Integer.parseInt(grainQuantityStr);
        } catch (NumberFormatException e) {
            editGQuantity.setError("Invalid grain quantity");
            return false;
        }

        // Check if location is empty
        if (location.isEmpty()) {
            editLocation.setError("Location is required");
            return false;
        }

        if (grainImageUri == null || scannerImageUri == null) {
            // Handle the case where one of the image URIs is null
            Toast.makeText(requireContext(), "Please select both grain and scanner images", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true; // All input fields are valid
    }


    private void addGrainToDatabase(String grainImageUrl, String scannerImageUrl) {

        String grainName = editGName.getText().toString();
        String grainVariety = editGVariety.getText().toString();
        double grainPrice = Double.parseDouble(editGPrice.getText().toString());
        int grainQuantity = Integer.parseInt(editGQuantity.getText().toString());
        String location = editLocation.getText().toString();
        String upiId = editupi.getText().toString();

        String fid = sessionManager.getKeySellerId();
        String grainId = databaseReference.push().getKey();

        GrainsDataModel grainsDataModel = new GrainsDataModel(grainId,fid, grainName, grainVariety, grainPrice, grainQuantity, location, upiId, grainImageUrl, getCurrentDate());

        databaseReference.child(grainId).setValue(grainsDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getContext(), "Grain added successfully!", Toast.LENGTH_SHORT).show();

                    replaceFragment(new SellerAddGrainFragment(), R.id.allCategories);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(requireContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        });

    }

    private void replaceFragment(Fragment fragment, int actionId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_seller_main);
        navController.navigate(actionId);
    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

}