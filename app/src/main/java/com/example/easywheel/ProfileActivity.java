package com.example.easywheel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ View-only fields
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvAge = findViewById(R.id.tvAge);
        TextView tvPhone = findViewById(R.id.tvPhone);

        // ✅ Editable fields
        EditText etBloodGroup = findViewById(R.id.etBloodGroup);
        EditText etMedicalCondition = findViewById(R.id.etMedicalCondition);
        EditText etAllergy = findViewById(R.id.etAllergy);

        EditText etEmergencyContact = findViewById(R.id.etEmergencyContact);
        EditText etDoctorName = findViewById(R.id.etDoctorName);
        EditText etDoctorPhone = findViewById(R.id.etDoctorPhone);

        Button btnUpdate = findViewById(R.id.btnUpdate);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            userId = user.getUid();

            // ✅ Basic Info
            tvEmail.setText("Email: " + user.getEmail());

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            // 🔹 Basic
                            tvUsername.setText("Username: " +
                                    documentSnapshot.getString("username"));

                            tvAge.setText("Age: " +
                                    documentSnapshot.getString("age"));

                            tvPhone.setText("Phone Number: " +
                                    documentSnapshot.getString("phone"));

                            // 🔹 Editable fields (set into EditText)
                            etBloodGroup.setText(documentSnapshot.getString("bloodGroup"));
                            etMedicalCondition.setText(documentSnapshot.getString("medicalCondition"));
                            etAllergy.setText(documentSnapshot.getString("allergy"));

                            etEmergencyContact.setText(documentSnapshot.getString("emergencyContact"));
                            etDoctorName.setText(documentSnapshot.getString("doctorName"));
                            etDoctorPhone.setText(documentSnapshot.getString("doctorPhone"));
                        }
                    });
        }

        // ✅ SAVE UPDATED DATA
        btnUpdate.setOnClickListener(v -> {

            Map<String, Object> updateData = new HashMap<>();

            updateData.put("bloodGroup", etBloodGroup.getText().toString());
            updateData.put("medicalCondition", etMedicalCondition.getText().toString());
            updateData.put("allergy", etAllergy.getText().toString());

            updateData.put("emergencyContact", etEmergencyContact.getText().toString());
            updateData.put("doctorName", etDoctorName.getText().toString());
            updateData.put("doctorPhone", etDoctorPhone.getText().toString());

            db.collection("users").document(userId)
                    .update(updateData)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Updated Successfully ✅", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update Failed ❌", Toast.LENGTH_SHORT).show());
        });
    }
}