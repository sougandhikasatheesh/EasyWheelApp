package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    String userRole;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        userRole = getIntent().getStringExtra("USER_ROLE");

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Intent intent;
                                if ("admin".equals(userRole)) {
                                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, MobilityDashboardActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
