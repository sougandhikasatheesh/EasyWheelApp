package com.example.easywheel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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

        // Dummy medicine data
        medicineList.add(new Medicine("Paracetamol", 50, "₹20"));
        medicineList.add(new Medicine("Dolo 650", 30,"₹30"));
        medicineList.add(new Medicine("Ibuprofen", 25,"₹35"));
        medicineList.add(new Medicine("Amoxicillin", 15,"₹40"));
        medicineList.add(new Medicine("Cetirizine", 40,"₹50"));

        filteredList.addAll(medicineList);

        adapter = new MedicineAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        setupSearch();
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
}