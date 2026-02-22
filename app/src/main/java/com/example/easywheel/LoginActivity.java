package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    String userRole;   // ✅ store selected role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ Get role from previous screen
        userRole = getIntent().getStringExtra("USER_ROLE");

        // Login fields
        EditText etUser = findViewById(R.id.etUser);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        // ✅ LOGIN BUTTON CLICK
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;

                // Redirect based on role
                if ("admin".equals(userRole)) {
                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);

                }
                else {
                    // default = mobility
                    intent = new Intent(LoginActivity.this, MobilityDashboardActivity.class);
                }

                startActivity(intent);
                finish();
            }
        });

        // ✅ CREATE ACCOUNT CLICK
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}