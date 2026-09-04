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

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerSchedule;
    private ScheduleAdapter scheduleAdapter;
    private ArrayList<Schedule> scheduleList;

    private DatabaseHelper databaseHelper;

    private ImageButton btnAdd;

    private TextView tvSun;
    private TextView tvMon;
    private TextView tvTue;
    private TextView tvWed;
    private TextView tvThu;
    private TextView tvFri;
    private TextView tvSat;
    private TextView tvTodayDate;

    private View emptyState;
    private MaterialButton btnEmptyAdd;

    private LinearLayout navHome;
    private LinearLayout navSubjects;
    private LinearLayout navCalendar;
    private LinearLayout navSchedule;
    private LinearLayout navSettings;

    private String selectedDay = "Monday";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerSchedule = findViewById(R.id.recyclerSchedule);
        btnAdd = findViewById(R.id.btnAdd);

        emptyState = findViewById(R.id.emptyState);
        btnEmptyAdd = findViewById(R.id.btnEmptyAdd);

        tvSun = findViewById(R.id.tvSun);
        tvMon = findViewById(R.id.tvMon);
        tvTue = findViewById(R.id.tvTue);
        tvWed = findViewById(R.id.tvWed);
        tvThu = findViewById(R.id.tvThu);
        tvFri = findViewById(R.id.tvFri);
        tvSat = findViewById(R.id.tvSat);
        tvTodayDate = findViewById(R.id.tvTodayDate);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navCalendar = findViewById(R.id.navCalendar);
        navSchedule = findViewById(R.id.navSchedule);
        navSettings = findViewById(R.id.navSettings);

        databaseHelper = new DatabaseHelper(this);

        recyclerSchedule.setLayoutManager(
                new LinearLayoutManager(this)
        );

        setTodayDate();
        selectToday();

        tvSun.setOnClickListener(v ->
                selectDay(tvSun, "Sunday")
        );

        tvMon.setOnClickListener(v ->
                selectDay(tvMon, "Monday")
        );

        tvTue.setOnClickListener(v ->
                selectDay(tvTue, "Tuesday")
        );

        tvWed.setOnClickListener(v ->
                selectDay(tvWed, "Wednesday")
        );

        tvThu.setOnClickListener(v ->
                selectDay(tvThu, "Thursday")
        );

        tvFri.setOnClickListener(v ->
                selectDay(tvFri, "Friday")
        );

        tvSat.setOnClickListener(v ->
                selectDay(tvSat, "Saturday")
        );

        btnAdd.setOnClickListener(v ->
                openAddSchedule()
        );

        btnEmptyAdd.setOnClickListener(v ->
                openAddSchedule()
        );

        setupBottomNavigation();
        highlightActiveTab(navSchedule);
    }

    private void setupBottomNavigation() {

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScheduleActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });

        navSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScheduleActivity.this,
                    SubjectsActivity.class
            );

            startActivity(intent);
            finish();
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScheduleActivity.this,
                    CalendarActivity.class
            );
            startActivity(intent);
            finish();
        });

        navSchedule.setOnClickListener(v -> {
            // Already on Schedule
        });

        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScheduleActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
            finish();
        });
    }

    private void highlightActiveTab(LinearLayout activeTab) {

        int activeColor = ContextCompat.getColor(this, R.color.brand_celtic_blue);
        int inactiveColor = ContextCompat.getColor(this, R.color.brutal_gray);

        setTabColor(navHome, inactiveColor);
        setTabColor(navSubjects, inactiveColor);
        setTabColor(navCalendar, inactiveColor);
        setTabColor(navSchedule, inactiveColor);
        setTabColor(navSettings, inactiveColor);

        setTabColor(activeTab, activeColor);
    }

    private void setTabColor(
            LinearLayout tab,
            int color
    ) {

        if (tab == null) {
            return;
        }

        for (int i = 0; i < tab.getChildCount(); i++) {

            View child = tab.getChildAt(i);

            if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
            }

            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(color);
            }
        }
    }

    private void setTodayDate() {

        String today = new SimpleDateFormat(
                "EEE • MMM dd",
                Locale.US
        ).format(new Date());

        tvTodayDate.setText(
                today.toUpperCase(Locale.US)
        );
    }

    private void openAddSchedule() {

        Intent intent = new Intent(
                ScheduleActivity.this,
                AddScheduleActivity.class
        );

        startActivity(intent);
    }

    private void selectToday() {

        String today = new SimpleDateFormat(
                "EEEE",
                Locale.US
        ).format(new Date());

        switch (today) {

            case "Sunday":
                selectDay(tvSun, "Sunday");
                break;

            case "Monday":
                selectDay(tvMon, "Monday");
                break;

            case "Tuesday":
                selectDay(tvTue, "Tuesday");
                break;

            case "Wednesday":
                selectDay(tvWed, "Wednesday");
                break;

            case "Thursday":
                selectDay(tvThu, "Thursday");
                break;

            case "Friday":
                selectDay(tvFri, "Friday");
                break;

            case "Saturday":
                selectDay(tvSat, "Saturday");
                break;

            default:
                selectDay(tvMon, "Monday");
                break;
        }
    }

    private void selectDay(
            TextView selectedButton,
            String day
    ) {

        selectedDay = day;

        // Reset all to unselected style
        int unselectedColor = ContextCompat.getColor(this, R.color.brand_dark_brown);
        tvSun.setTextColor(unselectedColor);
        tvMon.setTextColor(unselectedColor);
        tvTue.setTextColor(unselectedColor);
        tvWed.setTextColor(unselectedColor);
        tvThu.setTextColor(unselectedColor);
        tvFri.setTextColor(unselectedColor);
        tvSat.setTextColor(unselectedColor);

        tvSun.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvMon.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvTue.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvWed.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvThu.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvFri.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        tvSat.setBackgroundResource(
                R.drawable.bg_day_unselected
        );

        // Set selected style
        selectedButton.setTextColor(Color.BLACK);
        selectedButton.setBackgroundResource(
                R.drawable.bg_day_selected
        );

        loadSchedules(day);
    }

    private void loadSchedules(String day) {

        scheduleList =
                databaseHelper.getSchedulesByDay(day);

        if (scheduleList.isEmpty()) {

            recyclerSchedule.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);

        } else {

            recyclerSchedule.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            scheduleAdapter =
                    new ScheduleAdapter(scheduleList);

            recyclerSchedule.setAdapter(scheduleAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTodayDate();
        loadSchedules(selectedDay);
        highlightActiveTab(navSchedule);
    }
}