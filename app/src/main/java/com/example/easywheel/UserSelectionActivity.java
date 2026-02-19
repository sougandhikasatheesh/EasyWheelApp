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

        // Find the "Mobility User" layout
        LinearLayout btnMobility = findViewById(R.id.btnMobility);

        // Navigate to LoginActivity on click
        btnMobility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
