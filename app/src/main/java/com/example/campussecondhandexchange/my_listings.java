package com.example.campussecondhandexchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class my_listings extends AppCompatActivity {

    private RecyclerView recyclerViewMyListings;
    private LinearLayout emptyState;
    private MaterialButton btnStartSelling;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewMyListings = findViewById(R.id.recyclerViewMyListings);
        emptyState = findViewById(R.id.emptyState);
        btnStartSelling = findViewById(R.id.btnStartSelling);

        recyclerViewMyListings.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        // Reusing the same adapter built for the home feed
        productAdapter = new ProductAdapter(this, productList);
        recyclerViewMyListings.setAdapter(productAdapter);

        btnStartSelling.setOnClickListener(v -> {
            startActivity(new Intent(my_listings.this, add_listing.class));
            finish();
        });

        fetchMyListings();
    }

    private void fetchMyListings() {
        if (mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();

            // Query Firestore for listings matching the current user's ID
            db.collection("Listings")
                    .whereEqualTo("sellerId", currentUserId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        productList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }

                        // Toggle UI based on results
                        if (productList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewMyListings.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewMyListings.setVisibility(View.VISIBLE);
                            productAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(my_listings.this, "Error loading your items", Toast.LENGTH_SHORT).show());
        }
    }
}