package com.example.easywheel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MobilityDashboardActivity extends AppCompatActivity {

    private Button locationBtn;

    // ✅ Grid button layouts
    private LinearLayout btnToilet, btnHospital, btnMedicals, btnRepair, btnNgo, btnPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility_dashboard);

        // ✅ NEW: Check Location on App Start
        checkLocationEnabled();

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
        // ✅ Grid Click → Map
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

        btnMedicals.setOnClickListener(v -> {
            Intent intent = new Intent(MobilityDashboardActivity.this, MedicineSearchActivity.class);
            startActivity(intent);
        });

        btnRepair.setOnClickListener(v ->
                openMapWithType("wheelchair repair"));

        btnNgo.setOnClickListener(v ->
                openMapWithType("disability ngo"));

        btnPolice.setOnClickListener(v ->
                openMapWithType("police station"));
    }

    // ✅ NEW METHOD — Location Check
    private void checkLocationEnabled() {

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {

            new AlertDialog.Builder(this)
                    .setTitle("Enable Location")
                    .setMessage("Location is required for navigation and emergency services. Please turn on your location.")
                    .setCancelable(false)
                    .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // ✅ Helper — unchanged
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


    @Override
    protected void onResume() {
        super.onResume();
        checkLocationEnabled();
    }
}