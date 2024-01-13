package com.example.grainsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button sellerBtn, customerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sellerBtn = findViewById(R.id.sellerBtn);
        customerBtn = findViewById(R.id.customerBtn);

        sellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sellerIntent = new Intent(MainActivity.this, SellerLoginActivity.class);
                startActivity(sellerIntent);

            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sellerIntent = new Intent(MainActivity.this, BuyerMainActivity.class);
                startActivity(sellerIntent);

            }
        });


    }
}