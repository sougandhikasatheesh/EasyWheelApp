package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Onboarding1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding1);

        Button next = findViewById(R.id.btnNext);
        next.setOnClickListener(v -> {
            startActivity(new Intent(this, Onboarding2Activity.class));
        });
    }
}
