package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    String role;   // store user role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUser = findViewById(R.id.etUser);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);


        role = getIntent().getStringExtra("USER_ROLE");

        btnLogin.setOnClickListener(view -> {

            if ("admin".equals(role)) {

                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);

            } else if ("mobility".equals(role)) {

                Intent intent = new Intent(LoginActivity.this, MobilityDashboardActivity.class);
                startActivity(intent);

            }

            finish();
        });
    }
}