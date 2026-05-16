package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class UserSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        LinearLayout btnMobility = findViewById(R.id.btnMobility);
        LinearLayout btnAdmin = findViewById(R.id.btnAdmin);
        LinearLayout btnCaregiver = findViewById(R.id.btnCare);

        // Mobility User Click
        btnMobility.setOnClickListener(view -> {
            Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
            intent.putExtra("USER_ROLE", "mobility");
            startActivity(intent);
        });

        // Admin Click
        btnAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
            intent.putExtra("USER_ROLE", "admin");
            startActivity(intent);
        });
        btnCaregiver.setOnClickListener(view -> {
            Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
            intent.putExtra("USER_ROLE", "caregiver");
            startActivity(intent);
        });
    }
}