package com.example.studreminder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.Random;

public class FocusBlockerActivity extends AppCompatActivity {

    private final String[] quotes = {
            "The expert in anything was once a beginner.",
            "Study now, be proud later.",
            "Don't stop until you're proud.",
            "Focus on the goal, not the obstacle.",
            "Your future self will thank you."
    };

    private TextView tvTimer;
    private BroadcastReceiver timerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lock_overlay);

        // Local variables na ang mga UI components
        tvTimer = findViewById(R.id.tvTimer);
        TextView tvQuote = findViewById(R.id.tvQuote);
        MaterialButton btnUnlock = findViewById(R.id.btnUnlock);
        LinearLayout pinContainer = findViewById(R.id.pinContainer);
        EditText etPinInput = findViewById(R.id.etPinInput);
        MaterialButton btnSubmitPin = findViewById(R.id.btnSubmitPin);
        TextView btnEmergency = findViewById(R.id.btnEmergency);

        tvQuote.setText(quotes[new Random().nextInt(quotes.length)]);

        btnUnlock.setOnClickListener(v -> togglePinInput(true, pinContainer, btnUnlock, etPinInput));

        btnSubmitPin.setOnClickListener(v -> validatePinAndUnlock(etPinInput));

        btnEmergency.setOnClickListener(v -> stopFocusMode());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pinContainer.getVisibility() == View.VISIBLE) {
                    togglePinInput(false, pinContainer, btnUnlock, etPinInput);
                } else {
                    redirectToHome();
                }
            }
        });

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long remaining = intent.getLongExtra("remaining", 0);
                boolean isBreak = intent.getBooleanExtra("isBreak", false);

                if (isBreak) {
                    finish();
                } else {
                    updateTimerUI(remaining);
                }
            }
        };
    }

    private void togglePinInput(boolean show, LinearLayout pinContainer, View btnUnlock, EditText etPinInput) {
        if (show) {
            pinContainer.setVisibility(View.VISIBLE);
            btnUnlock.setVisibility(View.GONE);
            etPinInput.requestFocus();
        } else {
            pinContainer.setVisibility(View.GONE);
            btnUnlock.setVisibility(View.VISIBLE);
            etPinInput.getText().clear();
        }
    }

    private void validatePinAndUnlock(EditText etPinInput) {
        SharedPreferences prefs = getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);
        String savedPin = prefs.getString("EMERGENCY_PIN", "0000");
        String enteredPin = etPinInput.getText().toString().trim();

        if (enteredPin.equals(savedPin)) {
            Toast.makeText(this, R.string.toast_pin_correct, Toast.LENGTH_SHORT).show();
            stopFocusMode();
        } else {
            Toast.makeText(this, R.string.toast_incorrect_pin, Toast.LENGTH_SHORT).show();
            etPinInput.getText().clear();
        }
    }

    private void redirectToHome() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    private void updateTimerUI(long remaining) {
        int minutes = (int) (remaining / 1000) / 60;
        int seconds = (int) (remaining / 1000) % 60;
        tvTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Safe registration gamit ang ContextCompat
        ContextCompat.registerReceiver(
                this,
                timerReceiver,
                new IntentFilter("POMODORO_TICK"),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timerReceiver);
    }

    private void stopFocusMode() {
        FocusState.setBlockingActive(this, false);
        FocusState.endSession(this);
        stopService(new Intent(this, AppLockerService.class));
        Toast.makeText(this, R.string.toast_focus_disabled, Toast.LENGTH_LONG).show();

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_session_interrupted)
                .setMessage(R.string.dialog_reschedule_msg)
                .setPositiveButton("Reschedule", (dialog, which) -> finish())
                .setNegativeButton("Just Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}