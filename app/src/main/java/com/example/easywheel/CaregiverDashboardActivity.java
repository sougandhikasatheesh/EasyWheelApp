package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CaregiverDashboardActivity extends AppCompatActivity {

    // Dashboard features
    LinearLayout trackLayout, hospitalLayout, medicalLayout, repairLayout;

    // Bottom navigation
    LinearLayout bottomHome, bottomLocation, bottomChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_dashboard);

        // 🔹 Initialize Top Features
        trackLayout = findViewById(R.id.trackLayout);
        hospitalLayout = findViewById(R.id.hospitalLayout);
        medicalLayout = findViewById(R.id.medicalLayout);
        repairLayout = findViewById(R.id.repairLayout);

        // 🔹 Initialize Bottom Navigation
        LinearLayout bottomNav = findViewById(R.id.bottomNav);
        bottomHome = (LinearLayout) bottomNav.getChildAt(0);
        bottomLocation = (LinearLayout) bottomNav.getChildAt(1);
        bottomChat = (LinearLayout) bottomNav.getChildAt(2);




        // =========================
        // 🔻 BOTTOM NAVIGATION
        // =========================

        bottomHome.setOnClickListener(v -> {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        });

        bottomLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MapActivity.class));
        });

        
    }
}