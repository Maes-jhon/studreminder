package com.example.studreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddScheduleActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnSave;

    private TextInputEditText etSubject;
    private TextInputEditText etDate;
    private TextInputEditText etStartTime;
    private TextInputEditText etEndTime;
    private TextInputEditText etRoom;
    private MaterialAutoCompleteTextView dropdownReminder;

    private DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private int scheduleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        databaseHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        etSubject = findViewById(R.id.etSubject);
        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etRoom = findViewById(R.id.etRoom);
        dropdownReminder = findViewById(R.id.dropdownReminder);

        setupReminderDropdown();

        // Check for Edit Mode
        if (getIntent().hasExtra("schedule_id")) {
            isEditMode = true;
            scheduleId = getIntent().getIntExtra("schedule_id", -1);
            etSubject.setText(getIntent().getStringExtra("subject"));
            etDate.setText(getIntent().getStringExtra("date"));
            etStartTime.setText(getIntent().getStringExtra("start_time"));
            etEndTime.setText(getIntent().getStringExtra("end_time"));
            etRoom.setText(getIntent().getStringExtra("room"));
            String savedReminder = getIntent().getStringExtra("reminder");
            if (savedReminder != null) {
                dropdownReminder.setText(savedReminder, false);
            }
            btnSave.setText("Update Schedule");
        }

        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void setupReminderDropdown() {
        String[] reminders = {
                "5 minutes before",
                "15 minutes before",
                "30 minutes before",
                "1 hour before"
        };

        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reminders
        );

        dropdownReminder.setAdapter(reminderAdapter);
        dropdownReminder.setThreshold(0);
        dropdownReminder.setOnClickListener(v -> dropdownReminder.showDropDown());
        dropdownReminder.setText(reminders[1], false); // Default 15 mins
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Schedule Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.BrandDatePicker)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
            etDate.setText(format.format(calendar.getTime()));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker(TextInputEditText editText) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .setTitleText("Select Time")
                .setTheme(R.style.BrandTimePicker)
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = hour >= 12 ? "PM" : "AM";
            int hour12 = hour % 12;
            if (hour12 == 0) hour12 = 12;

            String time = String.format(Locale.getDefault(), "%d:%02d %s", hour12, minute, amPm);
            editText.setText(time);
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void saveSchedule() {
        String subject = etSubject.getText() != null ? etSubject.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String start = etStartTime.getText() != null ? etStartTime.getText().toString().trim() : "";
        String end = etEndTime.getText() != null ? etEndTime.getText().toString().trim() : "";
        String room = etRoom.getText() != null ? etRoom.getText().toString().trim() : "";
        String reminder = dropdownReminder.getText().toString().trim();

        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(date) || TextUtils.isEmpty(start) || TextUtils.isEmpty(end) || TextUtils.isEmpty(room) || TextUtils.isEmpty(reminder)) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        long resultId;
        if (isEditMode) {
            boolean updated = databaseHelper.updateSchedule(scheduleId, subject, date, start, end, room, reminder);
            resultId = updated ? scheduleId : -1;
        } else {
            resultId = databaseHelper.insertSchedule(subject, date, start, end, room, reminder);
        }

        if (resultId != -1) {
            scheduleClassAlarms((int) resultId, subject, date, start, room, reminder);
            Toast.makeText(this, isEditMode ? "Schedule Updated!" : "Schedule Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save schedule.", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleClassAlarms(int scheduleId, String subject, String date, String time, String room, String offsetStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a", Locale.US);
        try {
            Date classDate = sdf.parse(date + " " + time);
            if (classDate == null) return;

            long classTimeMillis = classDate.getTime();
            long reminderInterval = 0;

            if (offsetStr.contains("5 minutes")) reminderInterval = 5 * 60 * 1000;
            else if (offsetStr.contains("15 minutes")) reminderInterval = 15 * 60 * 1000;
            else if (offsetStr.contains("30 minutes")) reminderInterval = 30 * 60 * 1000;
            else if (offsetStr.contains("1 hour")) reminderInterval = 60 * 60 * 1000;

            long earlyTime = classTimeMillis - reminderInterval;

            if (earlyTime > System.currentTimeMillis()) {
                setAlarm(earlyTime, scheduleId, subject, room, false);
            }

            if (classTimeMillis > System.currentTimeMillis()) {
                setAlarm(classTimeMillis, scheduleId + 10000, subject, room, true);
            }
        } catch (Exception e) {
            Log.e("AddScheduleActivity", "Error scheduling class alarms", e);
        }
    }

    private void setAlarm(long triggerTime, int requestCode, String subject, String room, boolean isOnTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("subject", subject);
        intent.putExtra("topic", subject);
        intent.putExtra("room", room);
        intent.putExtra("isClassReminder", true);
        intent.putExtra("isFinalReminder", isOnTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}