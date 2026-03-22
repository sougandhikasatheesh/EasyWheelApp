package com.example.easywheel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easywheel.R;
import com.example.easywheel.SOSAdapter;
import com.example.easywheel.SOSModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CaregiverInboxActivity extends AppCompatActivity {

    RecyclerView recyclerSOS;
    List<SOSModel> list;
    SOSAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_inbox);

        recyclerSOS = findViewById(R.id.recyclerSOS);
        recyclerSOS.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new SOSAdapter(list);
        recyclerSOS.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sos_alerts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        list.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {

                            SOSModel model = doc.toObject(SOSModel.class);
                            list.add(model);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}