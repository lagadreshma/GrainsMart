package com.example.grainsmart;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grainsmart.databinding.ActivityBuyerMainBinding;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityBuyerMainBinding binding;
    CustomerSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBuyerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarBuyerMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        sessionManager = new CustomerSessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {

            // setting nav_header username and email

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

        }else{

            // setting nav_header username and email
            View header = navigationView.getHeaderView(0);
            TextView txtUserName  = (TextView) header.findViewById(R.id.navUserName);
            TextView txtUserEmailId = (TextView)header.findViewById(R.id.navUserEmail);
            CircleImageView profileImage = header.findViewById(R.id.navProfileImage);

            txtUserName.setText("Name");
            txtUserEmailId.setText("email@gmail.com");
            profileImage.setImageResource(R.drawable.my_profile);

        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_allCategories,R.id.nav_myOrders,R.id.nav_myProfile,
                R.id.nav_signIn, R.id.nav_signUp, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_buyer_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buyer_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_buyer_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}