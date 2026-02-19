package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Onboarding2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding2);

        Button next = findViewById(R.id.btnNext);

        next.setOnClickListener(v -> {
            startActivity(new Intent(this, Onboarding3Activity.class));
        });
    }
}
