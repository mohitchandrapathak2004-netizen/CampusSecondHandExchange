package com.example.campussecondhandexchange;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.material.button.MaterialButton;

public class product_details extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvCategory, tvCondition, tvDescription, tvSellerName, tvSellerPhone;
    private MaterialButton btnContactSeller;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Bind Views
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvCategory = findViewById(R.id.tvCategory);
        tvCondition = findViewById(R.id.tvCondition);
        tvDescription = findViewById(R.id.tvDescription);
        tvSellerName = findViewById(R.id.tvSellerName);
        tvSellerPhone = findViewById(R.id.tvSellerPhone);
        btnContactSeller = findViewById(R.id.btnContactSeller);

        // Get the Product object passed from the Adapter
        product = (Product) getIntent().getSerializableExtra("product_data");

        if (product != null) {
            populateData();
        } else {
            Toast.makeText(this, "Error loading product details", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Contact Seller Button - Opens Phone Dialer
        btnContactSeller.setOnClickListener(v -> {
            if (product.getSellerPhone() != null && !product.getSellerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + product.getSellerPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Seller phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData() {
        tvProductName.setText(product.getItemName());
        tvProductPrice.setText("₹" + product.getPrice());
        tvCategory.setText("Category: " + product.getCategory());
        tvCondition.setText("Condition: " + product.getCondition());
        tvDescription.setText(product.getDescription());
        tvSellerName.setText(product.getSellerName());
        tvSellerPhone.setText(product.getSellerPhone());

        // Load image with Glide
        Glide.with(this)
                .load(product.getImageUrl())
                .transform(new CenterCrop())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProductImage);
    }
}