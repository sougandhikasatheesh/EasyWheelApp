package com.example.easywheel;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedicineSearchActivity extends AppCompatActivity {

    EditText searchMedicine;
    Button btnSearch;
    RecyclerView resultRecycler;

    List<MedicineResult> resultList;
    MedicineResultAdapter adapter;

    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_search);

        searchMedicine = findViewById(R.id.searchMedicine);
        btnSearch = findViewById(R.id.btnSearch);
        resultRecycler = findViewById(R.id.resultRecycler);

        resultRecycler.setLayoutManager(new LinearLayoutManager(this));

        resultList = new ArrayList<>();
        adapter = new MedicineResultAdapter(resultList);
        resultRecycler.setAdapter(adapter);

        // Firebase reference
        databaseRef = FirebaseDatabase
                .getInstance("https://easywheel-38adf-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Medicines");

        // ✅ BUTTON CLICK DEBUG
        btnSearch.setOnClickListener(v -> {
            Log.d("CLICK", "BUTTON PRESSED");
            searchMedicine();
        });
    }

    private void searchMedicine() {

        // ✅ METHOD CALLED DEBUG
        Log.d("STEP", "searchMedicine() called");

        String input = searchMedicine.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            searchMedicine.setError("Enter medicine name");
            return;
        }

        String searchText = input.toLowerCase();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // ✅ FIREBASE DEBUG
                Log.d("STEP", "onDataChange triggered");
                Log.d("FIREBASE", "Children count: " + snapshot.getChildrenCount());

                resultList.clear();
                boolean found = false;

                for (DataSnapshot medicineSnapshot : snapshot.getChildren()) {

                    String medName = medicineSnapshot.getKey();

                    Log.d("FIREBASE", "Checking: " + medName);

                    if (medName != null &&
                            medName.toLowerCase().contains(searchText)) {

                        found = true;
                        Log.d("MATCH", "Found: " + medName);

                        for (DataSnapshot shopSnapshot : medicineSnapshot.getChildren()) {

                            String shop = shopSnapshot.getKey();

                            Integer quantity = shopSnapshot
                                    .child("quantity")
                                    .getValue(Integer.class);

                            Integer price = shopSnapshot
                                    .child("price")
                                    .getValue(Integer.class);

                            Log.d("DATA", "Shop: " + shop +
                                    " Price: " + price +
                                    " Qty: " + quantity);

                            if (shop != null && quantity != null && price != null) {

                                resultList.add(
                                        new MedicineResult(
                                                medName,
                                                shop,
                                                quantity,
                                                price
                                        )
                                );
                            }
                        }
                    }
                }

                // Sort results
                resultList.sort((a, b) -> a.getPrice() - b.getPrice());

                adapter.notifyDataSetChanged();

                if (!found) {
                    Toast.makeText(
                            MedicineSearchActivity.this,
                            "No medicine found",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.d("ERROR", error.getMessage());

                Toast.makeText(
                        MedicineSearchActivity.this,
                        "Database error",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}