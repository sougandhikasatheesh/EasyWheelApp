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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

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

        // Dialer buttons
        btnCallAmbulance.setOnClickListener(v -> openDialer("108"));
        btnCallPolice.setOnClickListener(v -> openDialer("100"));

        // SOS Button
        btnSos.setOnClickListener(v -> {
            sendEmergencyAlert();
        });
        btnCallCaregiver.setOnClickListener(v -> {
            fetchCaregivers();
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

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        String userId = com.google.firebase.auth.FirebaseAuth
                                .getInstance()
                                .getCurrentUser()
                                .getUid();

                        java.util.Map<String, Object> sosData = new java.util.HashMap<>();

                        sosData.put("userId", userId);
                        sosData.put("message", message);
                        sosData.put("latitude", currentLat);
                        sosData.put("longitude", currentLon);
                        sosData.put("timestamp", System.currentTimeMillis());

                        db.collection("sos_alerts")
                                .add(sosData)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(this, "Alert sent to caregivers", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to save alert", Toast.LENGTH_SHORT).show()
                                );

                    } else {
                        Toast.makeText(this,
                                "Unable to get location",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // UPDATED SMS METHOD (NO SmsManager, NO permission needed)
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

                            if (numbers.length() > 0) {
                                numbers.append(";");
                            }

                            numbers.append(phone);
                        }
                    }

                    if (numbers.length() > 0) {

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setData(Uri.parse("smsto:" + numbers.toString()));
                        smsIntent.putExtra("sms_body", message);

                        startActivity(smsIntent);

                    } else {
                        Toast.makeText(this,
                                "No caregivers found",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to fetch caregivers",
                                Toast.LENGTH_SHORT).show());
    }

    private void fetchCaregivers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("role", "caregiver")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> phones = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        String name = doc.getString("username");
                        String phone = doc.getString("phone");

                        if (name != null && phone != null) {
                            names.add(name);
                            phones.add(phone);
                        }
                    }

                    showCaregiverDialog(names, phones);

                });
    }
    private void showCaregiverDialog(ArrayList<String> names, ArrayList<String> phones) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Caregiver to Call");

        String[] nameArray = names.toArray(new String[0]);

        builder.setItems(nameArray, (dialog, which) -> {

            String phone = phones.get(which);

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);

        });

        builder.show();
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