package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CaregiverDashboardActivity extends AppCompatActivity {

    LinearLayout hospitalLayout, medicalLayout, repairLayout;
    ImageView home, location, chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_dashboard);

        // Features
        hospitalLayout = findViewById(R.id.hospitalLayout);
        medicalLayout = findViewById(R.id.medicalLayout);
        repairLayout = findViewById(R.id.repairLayout);

        // Bottom nav
        home = findViewById(R.id.ic_home);
        location = findViewById(R.id.ic_location);
        chat = findViewById(R.id.ic_chatmessage);

        // Feature clicks
        hospitalLayout.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class))
        );

        medicalLayout.setOnClickListener(v ->
                startActivity(new Intent(this, MedicineSearchActivity.class))
        );

        repairLayout.setOnClickListener(v ->
                Toast.makeText(this, "Repair coming soon", Toast.LENGTH_SHORT).show()
        );

        // Bottom nav clicks
        home.setOnClickListener(v ->
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        );

        location.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class))
        );

        chat.setOnClickListener(v ->
                startActivity(new Intent(this, CaregiverInboxActivity.class))
        );
    }
}