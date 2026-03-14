package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CaregiverDashboardActivity extends AppCompatActivity {

    ImageView emergencyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_dashboard);

        emergencyBtn = findViewById(R.id.emergencyBtn);

        emergencyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CaregiverDashboardActivity.this, EmergencyActivity.class);
            startActivity(intent);
        });
    }
}