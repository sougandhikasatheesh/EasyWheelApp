package com.example.easywheel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvCaregivers, tvWheelchairUsers;
    private RecyclerView recyclerView;
    private EditText searchMedicine;

    private MedicineAdapter adapter;
    private List<Medicine> medicineList;
    private List<Medicine> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // Initialize views
        tvCaregivers = findViewById(R.id.tvCaregivers);
        tvWheelchairUsers = findViewById(R.id.tvWheelchairUsers);
        recyclerView = findViewById(R.id.medicineRecyclerView);
        searchMedicine = findViewById(R.id.searchMedicine);

        // Set fixed overview values
        tvCaregivers.setText("👥 Total Caregivers : 12");
        tvWheelchairUsers.setText("🧑‍🦽 Total Wheelchair users : 5");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Dummy medicine data (UI display)
        medicineList.add(new Medicine("Paracetamol", 50, "₹20"));
        medicineList.add(new Medicine("Dolo 650", 30, "₹30"));
        medicineList.add(new Medicine("Ibuprofen", 25, "₹35"));
        medicineList.add(new Medicine("Amoxicillin", 15, "₹40"));
        medicineList.add(new Medicine("Cetirizine", 40, "₹50"));

        filteredList.addAll(medicineList);

        adapter = new MedicineAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        setupSearch();

        // =========================
        // ADD TEST DATA TO FIREBASE
        // =========================
        addTestMedicinesToFirebase();
    }

    // ===============================
    // Search Function
    // ===============================
    private void setupSearch() {

        searchMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedicines(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterMedicines(String text) {

        filteredList.clear();

        for (Medicine medicine : medicineList) {
            if (medicine.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(medicine);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // ===============================
    // FIREBASE TEST DATA FUNCTION
    // ===============================
    private void addTestMedicinesToFirebase() {

        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("medicines");

        // PARACETAMOL
        HashMap<String, Object> shop1 = new HashMap<>();
        shop1.put("shopName", "City Medicals");
        shop1.put("quantity", 50);
        shop1.put("price", 20);

        HashMap<String, Object> shop2 = new HashMap<>();
        shop2.put("shopName", "Apollo Pharmacy");
        shop2.put("quantity", 30);
        shop2.put("price", 18);

        HashMap<String, Object> shop3 = new HashMap<>();
        shop3.put("shopName", "MedPlus");
        shop3.put("quantity", 40);
        shop3.put("price", 22);

        databaseReference.child("paracetamol").child("shop1").setValue(shop1);
        databaseReference.child("paracetamol").child("shop2").setValue(shop2);
        databaseReference.child("paracetamol").child("shop3").setValue(shop3);


        // DOLO650
        HashMap<String, Object> shop4 = new HashMap<>();
        shop4.put("shopName", "City Medicals");
        shop4.put("quantity", 25);
        shop4.put("price", 35);

        HashMap<String, Object> shop5 = new HashMap<>();
        shop5.put("shopName", "Apollo Pharmacy");
        shop5.put("quantity", 15);
        shop5.put("price", 32);

        databaseReference.child("dolo650").child("shop1").setValue(shop4);
        databaseReference.child("dolo650").child("shop2").setValue(shop5);
    }
}