package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvAge = findViewById(R.id.tvAge);
        TextView tvPhone = findViewById(R.id.tvPhone);
        Button btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            String userId = user.getUid();
            tvEmail.setText("Email: " + user.getEmail());

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            tvUsername.setText("Username: " +
                                    documentSnapshot.getString("username"));

                            tvAge.setText("Age: " +
                                    documentSnapshot.getString("age"));

                            tvPhone.setText("Phone: " +
                                    documentSnapshot.getString("phone"));
                        }
                    });
        }

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(ProfileActivity.this,
                    LoginActivity.class);

            startActivity(intent);
            finish();
        });
    }
}