package com.example.studreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class AddReviewActivity extends AppCompatActivity {

    private static final String TAG = "AddReviewActivity";

    private ImageButton btnBack;
    private TextView tvTitle;

    private TextInputEditText etSubject;
    private TextInputEditText etTopic;
    private TextInputEditText etDate;
    private TextInputEditText etTime;

    private MaterialAutoCompleteTextView dropdownStatus;
    private MaterialAutoCompleteTextView dropdownReminder;
    private MaterialButton btnSaveReview;

    private DatabaseHelper databaseHelper;

    private boolean editMode = false;
    private int reviewId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        etSubject = findViewById(R.id.etSubject);
        etTopic = findViewById(R.id.etTopic);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);

        dropdownStatus = findViewById(R.id.dropdownStatus);
        dropdownReminder = findViewById(R.id.dropdownReminder);
        btnSaveReview = findViewById(R.id.btnSaveReview);

        databaseHelper = new DatabaseHelper(this);

        setupDropdowns();
        loadActivityData();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnBack.setOnClickListener(v -> finish());
        btnSaveReview.setOnClickListener(v -> saveOrUpdateReview());
    }

    private void setupDropdowns() {
        // Status Dropdown
        String[] statuses = {
                "Upcoming",
                "Due Soon",
                "Ongoing",
                "Completed",
                "Cancelled",
                "Rescheduled"
        };

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                statuses
        );
        dropdownStatus.setAdapter(statusAdapter);
        dropdownStatus.setThreshold(0);
        dropdownStatus.setOnClickListener(v -> dropdownStatus.showDropDown());
        dropdownStatus.setText(statuses[0], false); // Default: Upcoming

        // Reminder Dropdown
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
    }

    private void loadActivityData() {
        editMode = getIntent().getBooleanExtra("editMode", false);
        reviewId = getIntent().getIntExtra("reviewId", -1);

        String subject = getIntent().getStringExtra("subject");
        if (subject != null) {
            etSubject.setText(subject);
        }

        if (editMode) {
            tvTitle.setText(R.string.title_add_review);
            btnSaveReview.setText(R.string.btn_save_review);

            String topic = getIntent().getStringExtra("topic");
            String date = getIntent().getStringExtra("date");
            String time = getIntent().getStringExtra("time");
            String status = getIntent().getStringExtra("status");
            String reminder = getIntent().getStringExtra("reminder");

            if (topic != null) etTopic.setText(topic);
            if (date != null) etDate.setText(date);
            if (time != null) etTime.setText(time);
            if (status != null) dropdownStatus.setText(status, false);
            if (reminder != null) dropdownReminder.setText(reminder, false);

        } else {
            tvTitle.setText(R.string.title_add_review);
            btnSaveReview.setText(R.string.btn_save_review);
        }
    }

    private void saveOrUpdateReview() {
        String subject = etSubject.getText() == null ? "" : etSubject.getText().toString().trim();
        String topic = etTopic.getText() == null ? "" : etTopic.getText().toString().trim();
        String date = etDate.getText() == null ? "" : etDate.getText().toString().trim();
        String time = etTime.getText() == null ? "" : etTime.getText().toString().trim();
        String status = dropdownStatus.getText().toString().trim();
        String reminder = dropdownReminder.getText().toString().trim();

        if (subject.isEmpty() || topic.isEmpty() || date.isEmpty() || time.isEmpty() || status.isEmpty() || reminder.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editMode && reviewId != -1) {
            boolean updated = databaseHelper.updateReview(
                    reviewId,
                    subject,
                    topic,
                    date,
                    time,
                    reminder,
                    status
            );

            if (updated) {
                scheduleReminder(reviewId, subject, topic, date, time, reminder);
                Toast.makeText(this, "Review Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update review.", Toast.LENGTH_SHORT).show();
            }

        } else {
            long newId = databaseHelper.insertReview(
                    subject,
                    topic,
                    date,
                    time,
                    reminder,
                    status
            );

            if (newId != -1) {
                scheduleReminder((int) newId, subject, topic, date, time, reminder);
                Toast.makeText(this, "Review Saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save review.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Review Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.BrandDatePicker)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            etDate.setText(format.format(calendar.getTime()));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .setTitleText("Select Review Time")
                .setTheme(R.style.BrandTimePicker)
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = hour >= 12 ? "PM" : "AM";
            int hour12 = hour % 12;
            if (hour12 == 0) hour12 = 12;

            String formattedTime = String.format(Locale.getDefault(), "%d:%02d %s", hour12, minute, amPm);
            etTime.setText(formattedTime);
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void scheduleReminder(int reviewId, String subject, String topic, String date, String time, String reminder) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);
        try {
            Date reviewDate = sdf.parse(date + " " + time);
            if (reviewDate == null) return;

            long reviewTimeMillis = reviewDate.getTime();
            long reminderInterval = 0;

            if (reminder.contains("5 minutes")) reminderInterval = 5 * 60 * 1000;
            else if (reminder.contains("15 minutes")) reminderInterval = 15 * 60 * 1000;
            else if (reminder.contains("30 minutes")) reminderInterval = 30 * 60 * 1000;
            else if (reminder.contains("1 hour")) reminderInterval = 60 * 60 * 1000;

            long earlyTime = reviewTimeMillis - reminderInterval;

            if (earlyTime > System.currentTimeMillis()) {
                int earlyRequestCode = (reviewId + "_early").hashCode();
                setAlarm(earlyTime, earlyRequestCode, subject, topic, false, reviewId);
            }

            int finalRequestCode = (reviewId + "_final").hashCode();
            setAlarm(reviewTimeMillis, finalRequestCode, subject, topic, true, reviewId);

        } catch (Exception e) {
            Log.e(TAG, "Error scheduling reminder alarm", e);
        }
    }

    private void setAlarm(long triggerTime, int requestCode, String subject, String topic, boolean isFinal, int reviewId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("subject", subject);
        intent.putExtra("topic", topic);
        intent.putExtra("reviewId", reviewId);
        intent.putExtra("isFinalReminder", isFinal);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            Intent showIntent = new Intent(this, MainActivity.class);
            PendingIntent showPendingIntent = PendingIntent.getActivity(this, requestCode, showIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    } else {
                        startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                    }
                } else {
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException setting alarm clock", e);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}