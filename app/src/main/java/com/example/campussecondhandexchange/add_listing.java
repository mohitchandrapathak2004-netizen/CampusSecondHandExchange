package com.example.campussecondhandexchange;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.UUID;

public class add_listing extends AppCompatActivity {

    private ImageView ivProductImage;
    private MaterialButton btnAddImage, btnPostItem;
    private TextInputEditText etItemName, etPrice, etDescription;
    private AutoCompleteTextView actCategory, actCondition;

    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Handle Image Selection from Gallery
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivProductImage.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ivProductImage = findViewById(R.id.ivProductImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnPostItem = findViewById(R.id.btnPostItem);
        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        actCategory = findViewById(R.id.actCategory);
        actCondition = findViewById(R.id.actCondition);

        setupDropdowns();

        btnAddImage.setOnClickListener(v -> openGallery());

        btnPostItem.setOnClickListener(v -> {
            if (validateInput()) {
                uploadImageToCloudinary();
            }
        });
    }

    private void setupDropdowns() {
        String[] categories = {"Books", "Electronics", "Stationery", "Essentials", "Others"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(categoryAdapter);

        String[] conditions = {"Like New", "Good", "Fair", "Needs Repair"};
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, conditions);
        actCondition.setAdapter(conditionAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImage.launch(intent);
    }

    private boolean validateInput() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etItemName.getText().toString().trim().isEmpty() ||
                etPrice.getText().toString().trim().isEmpty() ||
                actCategory.getText().toString().trim().isEmpty() ||
                actCondition.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadImageToCloudinary() {
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();
        btnPostItem.setEnabled(false);

        MediaManager.get().upload(imageUri)
                .unsigned("campus_Exchange") // Triggers your specific upload preset
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        fetchSellerDetailsAndSave(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(add_listing.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        btnPostItem.setEnabled(true);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void fetchSellerDetailsAndSave(String imageUrl) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Fetch the user's profile data to attach to the listing
        db.collection("Users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String sellerName = documentSnapshot.getString("name");
                        String sellerPhone = documentSnapshot.getString("phone");
                        saveListingToFirestore(imageUrl, currentUserId, sellerName, sellerPhone);
                    } else {
                        Toast.makeText(add_listing.this, "User profile not found", Toast.LENGTH_SHORT).show();
                        btnPostItem.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(add_listing.this, "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPostItem.setEnabled(true);
                });
    }

    private void saveListingToFirestore(String imageUrl, String sellerId, String sellerName, String sellerPhone) {
        String productId = UUID.randomUUID().toString(); // Generate unique ID

        Product newProduct = new Product(
                productId,
                etItemName.getText().toString().trim(),
                actCategory.getText().toString().trim(),
                etPrice.getText().toString().trim(),
                actCondition.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                imageUrl,
                sellerId,
                sellerName,
                sellerPhone
        );

        db.collection("Listings").document(productId).set(newProduct)
                .addOnCompleteListener(task -> {
                    btnPostItem.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(add_listing.this, "Item posted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and return to feed
                    } else {
                        Toast.makeText(add_listing.this, "Failed to save listing data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}