package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CaregiverDashboardActivity extends AppCompatActivity {

    // Dashboard features
    private LinearLayout hospitalLayout, medicalLayout, repairLayout;

    // Bottom navigation
    private LinearLayout bottomHome, bottomLocation, bottomChat;
    private Button locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_dashboard);

        // ===== Location button =====
        locationBtn = findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CaregiverDashboardActivity.this, MapActivity.class);
            startActivityForResult(intent, 101);
        });

        // ===== Initialize top features =====
        hospitalLayout = findViewById(R.id.hospitalLayout);
        medicalLayout = findViewById(R.id.medicalLayout);
        repairLayout = findViewById(R.id.repairLayout);

        // Hospital → MapActivity
        hospitalLayout.setOnClickListener(v -> openMapWithType("hospital"));

        // Medicals → MedicineSearchActivity
        medicalLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CaregiverDashboardActivity.this, MedicineSearchActivity.class);
            startActivity(intent);
        });

        // Repair → MapActivity
        repairLayout.setOnClickListener(v -> openMapWithType("wheelchair repair"));

        // ===== Initialize Bottom Navigation =====
        LinearLayout bottomNav = findViewById(R.id.bottomNav);
        bottomHome = (LinearLayout) bottomNav.getChildAt(0);
        bottomLocation = (LinearLayout) bottomNav.getChildAt(1);
        bottomChat = (LinearLayout) bottomNav.getChildAt(2);

        bottomHome.setOnClickListener(v ->
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());

        bottomLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MapActivity.class));
        });

        ImageView chatIcon = findViewById(R.id.ic_chatmessage);
        chatIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CaregiverDashboardActivity.this,
                    CaregiverInboxActivity.class);
            startActivity(intent);
        });
    }

    // ===== Helper method to open MapActivity with place type =====
    private void openMapWithType(String type) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("PLACE_TYPE", type);
        startActivity(intent);
    }

    // ===== Receive location from MapActivity =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            locationBtn.setText(address != null ? address : "Location unavailable");
        }
    }
}