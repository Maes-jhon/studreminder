package com.example.studreminder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    private RecyclerView recyclerSubjects;
    private SubjectAdapter adapter;
    private ArrayList<Subject> subjectList;
    private DatabaseHelper databaseHelper;

    private ImageButton btnAddSubject;

    private LinearLayout navHome;
    private LinearLayout navSubjects;
    private LinearLayout navCalendar;
    private LinearLayout navSchedule;
    private LinearLayout navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        recyclerSubjects = findViewById(R.id.recyclerSubjects);
        btnAddSubject = findViewById(R.id.btnAddSubject);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navCalendar = findViewById(R.id.navCalendar);
        navSchedule = findViewById(R.id.navSchedule);
        navSettings = findViewById(R.id.navSettings);

        databaseHelper = new DatabaseHelper(this);

        recyclerSubjects.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSubjects();

        btnAddSubject.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SubjectsActivity.this,
                            AddSubjectActivity.class
                    )
            );

        });

        setupBottomNavigation();
        highlightTab(navSubjects);
    }

    private void loadSubjects() {

        subjectList = databaseHelper.getAllSubjects();

        adapter = new SubjectAdapter(subjectList);

        recyclerSubjects.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSubjects();
        highlightTab(navSubjects);
    }

    private void setupBottomNavigation() {

        navHome.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            MainActivity.class
                    )
            );

            finish();

        });

        navSubjects.setOnClickListener(v -> {
            // Already here
        });

        navCalendar.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            this,
                            CalendarActivity.class
                    )
            );
            finish();
        });

        navSchedule.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            ScheduleActivity.class
                    )
            );

            finish();

        });

        navSettings.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            this,
                            SettingsActivity.class
                    )
            );
            finish();
        });

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