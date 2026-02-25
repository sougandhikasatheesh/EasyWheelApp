package com.example.easywheel;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etUserName = findViewById(R.id.etUserName);
        EditText etUserId = findViewById(R.id.etUserId);
        EditText etAge = findViewById(R.id.etAge);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {

            String username = etUserName.getText().toString().trim();
            String email = etUserId.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etUserId.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            String userId = mAuth.getCurrentUser().getUid();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("age", age);
                            userMap.put("phone", phone);
                            userMap.put("email", email);

                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(CreateAccountActivity.this,
                                                "Account Created Successfully!",
                                                Toast.LENGTH_SHORT).show();

                                        finish(); // go back to login

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CreateAccountActivity.this,
                                                "Failed to save user data",
                                                Toast.LENGTH_SHORT).show();
                                    });

                        } else {

                            Toast.makeText(CreateAccountActivity.this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}