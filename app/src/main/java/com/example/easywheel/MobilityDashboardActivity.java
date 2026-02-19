package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MobilityDashboardActivity extends AppCompatActivity {

    private Button locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility_dashboard);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                // Already on home screen
                return true;
            }

            if (item.getItemId() == R.id.nav_location) {
                Intent intent = new Intent(MobilityDashboardActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_emergency) {
                // Example: open dialer
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_profile) {
                // Later you can create ProfileActivity
                return true;
            }

            return false;
        });


        locationBtn = findViewById(R.id.locationBtn);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobilityDashboardActivity.this, MapActivity.class);
                startActivityForResult(intent, 101); // request code 101
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            locationBtn.setText(address != null ? address : "Location unavailable");
        }
    }
}
