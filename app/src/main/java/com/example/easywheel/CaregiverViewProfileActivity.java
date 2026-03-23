package com.example.easywheel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class CaregiverViewProfileActivity extends AppCompatActivity {

    TextView tvName, tvAge, tvPhone, tvBlood, tvMedical, tvAllergy,
            tvEmergency, tvDoctorName, tvDoctorPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_profile);

        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvPhone = findViewById(R.id.tvPhone);
        tvBlood = findViewById(R.id.tvBlood);
        tvMedical = findViewById(R.id.tvMedical);
        tvAllergy = findViewById(R.id.tvAllergy);
        tvEmergency = findViewById(R.id.tvEmergency);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvDoctorPhone = findViewById(R.id.tvDoctorPhone);

        String userId = getIntent().getStringExtra("USER_ID");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        tvName.setText("Name: " + doc.getString("username"));
                        tvAge.setText("Age: " + doc.getString("age"));
                        tvPhone.setText("Phone: " + doc.getString("phone"));

                        tvBlood.setText("Blood Group: " + doc.getString("bloodGroup"));
                        tvMedical.setText("Medical: " + doc.getString("medicalCondition"));
                        tvAllergy.setText("Allergy: " + doc.getString("allergy"));

                        tvEmergency.setText("Emergency Contact: " + doc.getString("emergencyContact"));
                        tvDoctorName.setText("Doctor: " + doc.getString("doctorName"));
                        tvDoctorPhone.setText("Doctor Phone: " + doc.getString("doctorPhone"));
                    }
                });
    }
}