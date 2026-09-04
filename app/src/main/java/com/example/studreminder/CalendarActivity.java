package com.example.studreminder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private LinearLayout navHome, navSubjects, navCalendar, navSchedule, navSettings;
    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private RecyclerView rvDailySchedule;
    private DatabaseHelper dbHelper;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new DatabaseHelper(this);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navCalendar = findViewById(R.id.navCalendar);
        navSchedule = findViewById(R.id.navSchedule);
        navSettings = findViewById(R.id.navSettings);

        calendarView = findViewById(R.id.mainCalendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rvDailySchedule = findViewById(R.id.rvDailySchedule);

        rvDailySchedule.setLayoutManager(new LinearLayoutManager(this));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            loadScheduleForDate(calendar.getTime());
        });

        setupBottomNavigation();
        highlightTab(navCalendar);

        // Initial Load for Today
        loadScheduleForDate(new Date());
    }

    private void loadScheduleForDate(Date date) {
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tvSelectedDate.setText("Schedule for " + sdfDisplay.format(date));

        SimpleDateFormat sdfSearch = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String dbSearchString = sdfSearch.format(date);

        ArrayList<Schedule> dailySched = dbHelper.getSchedulesByDay(dbSearchString);
        adapter = new ScheduleAdapter(dailySched);
        rvDailySchedule.setAdapter(adapter);
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
        navCalendar.setOnClickListener(v -> {});
        navSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, ScheduleActivity.class));
            finish();
        });
        navSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
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