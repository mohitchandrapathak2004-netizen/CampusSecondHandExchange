package com.example.campussecondhandexchange;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private RecyclerView recyclerViewListings;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigation;
    private ImageButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        recyclerViewListings = findViewById(R.id.recyclerViewListings);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnProfile = findViewById(R.id.btnProfile);

        // Setup RecyclerView
        recyclerViewListings.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        recyclerViewListings.setAdapter(productAdapter);

        // Load Data
        fetchListings();

        // Top Profile Button
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(home.this, Profile.class));
        });

        // Bottom Navigation Setup
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(home.this, add_listing.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(home.this, Profile.class));
                return true;
            }
            return false;
        });
    }

    private void fetchListings() {
        db.collection("Listings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(home.this, "Error loading items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}