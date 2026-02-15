package com.example.easywheel;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;



public class Onboarding3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding3);

        Button btnStart = findViewById(R.id.btnGetStarted);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding3Activity.this, UserSelectionActivity.class);
            startActivity(intent);
        });

    }
}
