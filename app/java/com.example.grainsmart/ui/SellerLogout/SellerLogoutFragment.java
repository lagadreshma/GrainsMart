package com.example.grainsmart.ui.SellerLogout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grainsmart.MainActivity;
import com.example.grainsmart.R;
import com.example.grainsmart.SellerLoginActivity;
import com.example.grainsmart.SessionManager;
import com.example.grainsmart.databinding.ActivitySellerMainBinding;
import com.example.grainsmart.ui.SignIn.BuyerSignInFragment;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerLogoutFragment extends Fragment {

    AppCompatButton logoutBtn;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_logout, container, false);

        sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {

            Toast.makeText(getContext(), "Login Please", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), SellerLoginActivity.class);
            startActivity(intent);

        }

        logoutBtn = view.findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.logoutUser();

                // Clear the back stack and start the MainActivity
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();

            }
        });


        return view;
    }

}