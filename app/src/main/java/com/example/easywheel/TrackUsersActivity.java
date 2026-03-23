package com.example.easywheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrackUsersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> names;
    private ArrayList<String> userIds;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_users);

        ListView listView = findViewById(R.id.listView);

        db = FirebaseFirestore.getInstance();
        names = new ArrayList<>();
        userIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);

        listView.setAdapter(adapter);

        // 🔥 Fetch tracked users
        db.collection("trackUsers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (var doc : queryDocumentSnapshots) {

                        String name = doc.getString("username");
                        String userId = doc.getString("userId");

                        names.add(name != null ? name : "Unknown User");
                        userIds.add(userId);
                    }

                    adapter.notifyDataSetChanged();
                });

        // 👉 Click → open profile
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(this,
                    CaregiverViewProfileActivity.class);

            intent.putExtra("USER_ID", userIds.get(position));
            startActivity(intent);
        });
    }
}