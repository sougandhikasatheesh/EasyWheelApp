package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MobilityDashboardActivity extends AppCompatActivity {

    private Button locationBtn;

    // ✅ Grid button layouts (these are VIEW IDs, not drawable names)
    private LinearLayout btnToilet, btnHospital, btnMedicals, btnRepair, btnNgo, btnPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                return true;
            }

            if (item.getItemId() == R.id.nav_location) {
                Intent intent = new Intent(MobilityDashboardActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_emergency) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(MobilityDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_emergency_page) {
                Intent intent = new Intent(MobilityDashboardActivity.this, EmergencyActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });

        // ✅ EXISTING — unchanged
        locationBtn = findViewById(R.id.locationBtn);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobilityDashboardActivity.this, MapActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        // =========================
        // ✅ NEW FEATURE — Grid Click → Map
        // =========================

        btnToilet = findViewById(R.id.btnToilet);
        btnHospital = findViewById(R.id.btnHospital);
        btnMedicals = findViewById(R.id.btnMedicals);
        btnRepair = findViewById(R.id.btnRepair);
        btnNgo = findViewById(R.id.btnNgo);
        btnPolice = findViewById(R.id.btnPolice);

        btnToilet.setOnClickListener(v ->
                openMapWithType("wheelchair accessible toilet"));

        btnHospital.setOnClickListener(v ->
                openMapWithType("hospital"));

        btnMedicals.setOnClickListener(v ->
                openMapWithType("pharmacy"));

        btnRepair.setOnClickListener(v ->
                openMapWithType("wheelchair repair"));

        btnNgo.setOnClickListener(v ->
                openMapWithType("disability ngo"));

        btnPolice.setOnClickListener(v ->
                openMapWithType("police station"));
    }

    // ✅ Helper — does not affect existing location logic
    private void openMapWithType(String type) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("PLACE_TYPE", type);
        startActivity(intent);
    }

    // ✅ EXISTING — unchanged
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            locationBtn.setText(address != null ? address : "Location unavailable");
        }
    }
}