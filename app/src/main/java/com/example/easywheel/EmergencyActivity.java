package com.example.easywheel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        MaterialButton btnCallCaregiver = findViewById(R.id.btnCallCaregiver);

        // Dialer
        btnCallAmbulance.setOnClickListener(v -> openDialer("108"));
        btnCallPolice.setOnClickListener(v -> openDialer("100"));

        // SOS
        btnSos.setOnClickListener(v -> sendEmergencyAlert());

        // Call caregiver
        btnCallCaregiver.setOnClickListener(v -> fetchCaregivers());

        // Live location toggle
        switchLiveLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) startLiveLocationUpdates();
            else stopLiveLocationUpdates();
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

    // =========================
    // 📞 Dialer
    // =========================
    private void openDialer(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    // =========================
    // 🚨 SOS FUNCTION (UPDATED)
    // =========================
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
                    if (location == null) {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();

                    String userId = FirebaseAuth.getInstance()
                            .getCurrentUser().getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(document -> {

                                String username = document.getString("username");
                                if (username == null) username = "Unknown User";

                                String mapsLink = "https://maps.google.com/?q="
                                        + currentLat + "," + currentLon;

                                String message = "User: " + username;

                                // ✅ SEND SMS
                                sendSMS(message + "\nLocation: " + mapsLink);

                                // ✅ SAVE SOS ALERT
                                Map<String, Object> sosData = new HashMap<>();
                                sosData.put("userId", userId);
                                sosData.put("username", username);
                                sosData.put("locationLink", mapsLink);
                                sosData.put("lat", currentLat);
                                sosData.put("lon", currentLon);
                                sosData.put("timestamp", System.currentTimeMillis());

                                db.collection("sos_alerts").add(sosData);

                                // 🔥 ADD TO TRACK USERS
                                Map<String, Object> trackData = new HashMap<>();
                                trackData.put("userId", userId);
                                trackData.put("username", username);
                                trackData.put("lat", currentLat);
                                trackData.put("lon", currentLon);
                                trackData.put("locationLink", mapsLink);
                                trackData.put("timestamp", System.currentTimeMillis());

                                db.collection("trackUsers")
                                        .document(userId)
                                        .set(trackData);

                                Toast.makeText(this,
                                        "SOS Sent & Tracking Enabled 🚨",
                                        Toast.LENGTH_SHORT).show();

                            });
                });
    }

    // =========================
    // 📩 SEND SMS
    // =========================
    private void sendSMS(String message) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("role", "caregiver")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    StringBuilder numbers = new StringBuilder();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String phone = doc.getString("phone");

                        if (phone != null && !phone.isEmpty()) {
                            if (numbers.length() > 0) numbers.append(";");
                            numbers.append(phone);
                        }
                    }

                    if (numbers.length() > 0) {

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setData(Uri.parse("smsto:" + numbers));
                        smsIntent.putExtra("sms_body", message);
                        startActivity(smsIntent);

                    } else {
                        Toast.makeText(this, "No caregivers found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // =========================
    // 📞 CAREGIVER LIST
    // =========================
    private void fetchCaregivers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("role", "caregiver")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> phones = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        names.add(doc.getString("username"));
                        phones.add(doc.getString("phone"));
                    }

                    showCaregiverDialog(names, phones);
                });
    }

    private void showCaregiverDialog(ArrayList<String> names, ArrayList<String> phones) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Caregiver");

        builder.setItems(names.toArray(new String[0]), (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phones.get(which)));
            startActivity(intent);
        });

        builder.show();
    }

    // =========================
    // 📍 LIVE LOCATION
    // =========================
    private void startLiveLocationUpdates() {

        LocationRequest request = LocationRequest.create()
                .setInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                Location location = result.getLastLocation();
                if (location != null) {

                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();

                    // 🔥 Update Firestore live
                    FirebaseFirestore.getInstance()
                            .collection("trackUsers")
                            .document(FirebaseAuth.getInstance().getUid())
                            .update("lat", currentLat,
                                    "lon", currentLon);
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        }
    }

    private void stopLiveLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}