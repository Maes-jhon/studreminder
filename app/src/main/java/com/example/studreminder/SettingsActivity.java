package com.example.studreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout navHome, navSubjects, navCalendar, navSchedule, navSettings;
    private MaterialCardView cardSelectApps, cardSetPin, cardArchivedItems;
    private MaterialButton btnSettingsExit, btnSettingsLogout;
    private TextView tvCurrentPin;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);

        // Navigation
        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navCalendar = findViewById(R.id.navCalendar);
        navSchedule = findViewById(R.id.navSchedule);
        navSettings = findViewById(R.id.navSettings);

        // Buttons
        cardSelectApps = findViewById(R.id.cardSelectApps);
        cardArchivedItems = findViewById(R.id.cardArchivedItems);
        cardSetPin = findViewById(R.id.cardSetPin);
        btnSettingsLogout = findViewById(R.id.btnSettingsLogout);
        btnSettingsExit = findViewById(R.id.btnSettingsExit);
        
        tvCurrentPin = findViewById(R.id.tvCurrentPin);
        tvCurrentPin.setText(prefs.getString("EMERGENCY_PIN", "1234"));

        if (cardSelectApps != null) {
            cardSelectApps.setOnClickListener(v -> {
                startActivity(new Intent(this, AppSelectionActivity.class));
            });
        }

        cardSetPin.setOnClickListener(v -> showPinDialog());

        cardArchivedItems.setOnClickListener(v -> {
            startActivity(new Intent(this, ArchivedItemsActivity.class));
        });

        btnSettingsLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        getSharedPreferences("session", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnSettingsExit.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to close the application?")
                    .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        setupBottomNavigation();
        highlightTab(navSettings);
    }

    private void showPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Emergency PIN");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Enter 4-digit PIN");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPin = input.getText().toString();
            if (newPin.length() == 4) {
                prefs.edit().putString("EMERGENCY_PIN", newPin).apply();
                tvCurrentPin.setText(newPin);
                Toast.makeText(this, "PIN Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        navSubjects.setOnClickListener(v -> {
            startActivity(new Intent(this, SubjectsActivity.class));
            finish();
        });
        navCalendar.setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarActivity.class));
            finish();
        });
        navSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, ScheduleActivity.class));
            finish();
        });
        navSettings.setOnClickListener(v -> {});
    }

    private void highlightTab(LinearLayout activeTab) {
        int active = ContextCompat.getColor(this, R.color.brand_celtic_blue);
        int inactive = ContextCompat.getColor(this, R.color.brutal_gray);
        colorTab(navHome, inactive);
        colorTab(navSubjects, inactive);
        colorTab(navCalendar, inactive);
        colorTab(navSchedule, inactive);
        colorTab(navSettings, inactive);
        colorTab(activeTab, active);
    }

    private void colorTab(LinearLayout tab, int color) {
        if (tab == null) return;
        for (int i = 0; i < tab.getChildCount(); i++) {
            View child = tab.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(color);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
            }
        }
    }
}