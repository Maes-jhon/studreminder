package com.example.studreminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SubjectDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack, btnShare;

    private TextView tvSubject;
    private TextView tvTeacher;
    private TextView tvRoom;
    private TextView tvNoReviews;

    private RecyclerView recyclerReviews;
    private MaterialButton btnAddReview, btnStartFocusSession;

    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviewList;
    private DatabaseHelper databaseHelper;

    private String subject;
    private String teacher;
    private String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);

        tvSubject = findViewById(R.id.tvSubject);
        tvTeacher = findViewById(R.id.tvTeacher);
        tvRoom = findViewById(R.id.tvRoom);
        tvNoReviews = findViewById(R.id.tvNoReviews);

        recyclerReviews = findViewById(R.id.recyclerReviews);
        btnAddReview = findViewById(R.id.btnAddReview);
        btnStartFocusSession = findViewById(R.id.btnStartFocusSession);

        databaseHelper = new DatabaseHelper(this);

        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        subject = intent.getStringExtra("subject");
        teacher = intent.getStringExtra("teacher");
        room = intent.getStringExtra("room");

        if (subject == null) subject = "";
        if (teacher == null) teacher = "";
        if (room == null) room = "";

        tvSubject.setText(subject);
        tvTeacher.setText(teacher);
        tvRoom.setText(room);

        btnBack.setOnClickListener(v -> finish());

        btnShare.setOnClickListener(v -> {
            String shareBody = "Hey! Check out my class schedule:\n\n" +
                    "Subject: " + subject + "\n" +
                    "Instructor: " + teacher + "\n" +
                    "Room: " + room + "\n\n" +
                    "Sent from Student Reminder App";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Class Schedule: " + subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share Class Schedule"));
        });

        btnAddReview.setOnClickListener(v -> {
            Intent reviewIntent = new Intent(SubjectDetailsActivity.this, AddReviewActivity.class);
            reviewIntent.putExtra("subject", subject);
            startActivity(reviewIntent);
        });

        if (btnStartFocusSession != null) {
            btnStartFocusSession.setOnClickListener(v -> toggleFocusSession());
        }
    }

    private void toggleFocusSession() {
        if (FocusState.isSessionActive(this)) {
            FocusState.endSession(this);
            Toast.makeText(this, "Focus Session Ended", Toast.LENGTH_SHORT).show();
            updateFocusButtonState();
        } else {
            // Check Accessibility Service
            if (!isAccessibilityServiceEnabled(this, FocusAccessibilityService.class)) {
                Toast.makeText(this, "Please enable Accessibility Service to lock distracting apps", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }

            // Check Display Over Other Apps Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please allow 'Display over other apps' permission", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }

            SharedPreferences lockerPrefs = getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);
            Set<String> blockedApps = lockerPrefs.getStringSet("LOCKED_APPS_SET", new HashSet<>());

            if (blockedApps.isEmpty()) {
                Toast.makeText(this, "No apps selected to lock! Set them in App Selection first.", Toast.LENGTH_LONG).show();
                return;
            }

            FocusState.startSession(this, subject, blockedApps);
            Toast.makeText(this, "Focus Mode Started for " + subject, Toast.LENGTH_SHORT).show();
            updateFocusButtonState();
        }
    }

    private void updateFocusButtonState() {
        if (btnStartFocusSession == null) return;

        if (FocusState.isSessionActive(this)) {
            btnStartFocusSession.setText(R.string.btn_end_mission);
            btnStartFocusSession.setBackgroundResource(R.drawable.brutal_button_red);
        } else {
            btnStartFocusSession.setText(R.string.btn_start_mission);
            btnStartFocusSession.setBackgroundResource(R.drawable.brutal_button);
        }
    }

    private boolean isAccessibilityServiceEnabled(Context context, Class<?> serviceClass) {
        String prefString = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return prefString != null && prefString.contains(context.getPackageName() + "/" + serviceClass.getName());
    }

    private void loadReviews() {
        reviewList = databaseHelper.getReviewsBySubject(subject);

        if (reviewList.isEmpty()) {
            recyclerReviews.setVisibility(View.GONE);
            tvNoReviews.setVisibility(View.VISIBLE);
        } else {
            recyclerReviews.setVisibility(View.VISIBLE);
            tvNoReviews.setVisibility(View.GONE);

            reviewAdapter = new ReviewAdapter(reviewList);
            recyclerReviews.setAdapter(reviewAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews();
        updateFocusButtonState();
    }
}