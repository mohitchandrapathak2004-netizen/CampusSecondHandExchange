package com.example.campussecondhandexchange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Cloudinary
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "rauem9k6");
            MediaManager.init(this, config);
        } catch (Exception e) {
            // Ignore if already initialized
        }

        // 2-second delay for the splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // User is already logged in, go straight to home
                startActivity(new Intent(splash.this, home.class));
            } else {
                // Not logged in, go to login screen
                startActivity(new Intent(splash.this, login.class));
            }
            finish();
        }, 2000);
    }
}