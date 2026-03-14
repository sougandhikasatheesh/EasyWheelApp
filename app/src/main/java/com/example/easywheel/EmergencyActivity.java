package com.example.easywheel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class EmergencyActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int REQUEST_LOCATION_PERMISSION = 101;

    private double currentLat = 0.0;
    private double currentLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Buttons
        MaterialButton btnCallAmbulance = findViewById(R.id.btnCallAmbulance);
        MaterialButton btnCallPolice = findViewById(R.id.btnCallPolice);
        ImageButton btnSos = findViewById(R.id.btnSos);
        SwitchCompat switchLiveLocation = findViewById(R.id.switchLiveLocation);

        // Dialer buttons
        btnCallAmbulance.setOnClickListener(v -> openDialer("108"));
        btnCallPolice.setOnClickListener(v -> openDialer("100"));

        // SOS Button
        btnSos.setOnClickListener(v -> {
            openDialer("112");
            sendEmergencyAlert();
        });

        // Live location switch
        switchLiveLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startLiveLocationUpdates();
            } else {
                stopLiveLocationUpdates();
            }
        });

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_emergency);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MobilityDashboardActivity.class));
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_location) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            }

            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return true;
        });
    }

    // Open Dialer
    private void openDialer(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    // Send Emergency SMS (UPDATED SAFE METHOD)
    private void sendEmergencyAlert() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {

                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();

                        String mapsLink = "https://maps.google.com/?q="
                                + currentLat + "," + currentLon;

                        String message = "🚨 EMERGENCY! I need help.\nMy Location:\n" + mapsLink;

                        sendSMS(message);

                    } else {
                        Toast.makeText(this,
                                "Unable to get location",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // UPDATED SMS METHOD (NO SmsManager, NO permission needed)
    private void sendSMS(String message) {

        String phoneNumber = "8891230108"; // caregiver number

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);

        startActivity(smsIntent);
    }

    // Start Live Location Updates
    private void startLiveLocationUpdates() {

        LocationRequest locationRequest =
                LocationRequest.create()
                        .setInterval(10000)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
            );
        }
    }

    private void stopLiveLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}