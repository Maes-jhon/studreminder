package com.example.studreminder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Timed splash for a better UX
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getSharedPreferences("session", MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            }
            finish();
        }, 2000);
    }
}