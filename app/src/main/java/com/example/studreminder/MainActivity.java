package com.example.studreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navSubjects, navCalendar, navSchedule, navSettings;
    private TextView tvGreeting, tvName, tvDate;
    private TextView tvSubject, tvTime;
    private TextView tvReviewSubject, tvReviewTime;
    private TextView tvLevel, tvRank;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressXP;

    private View layoutPermissionBanner;
    private TextView tvPermissionMessage;
    private Button btnFixPermissions;

    private DatabaseHelper databaseHelper;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);

        tvSubject = findViewById(R.id.tvSubject);
        tvTime = findViewById(R.id.tvTime);

        tvReviewSubject = findViewById(R.id.tvReviewSubject);
        tvReviewTime = findViewById(R.id.tvReviewTime);

        tvLevel = findViewById(R.id.tvLevel);
        tvRank = findViewById(R.id.tvRank);
        progressXP = findViewById(R.id.progressXP);

        layoutPermissionBanner = findViewById(R.id.layoutPermissionBanner);
        tvPermissionMessage = findViewById(R.id.tvPermissionMessage);
        btnFixPermissions = findViewById(R.id.btnFixPermissions);

        navHome = findViewById(R.id.navHome);
        navSubjects = findViewById(R.id.navSubjects);
        navCalendar = findViewById(R.id.navCalendar);
        navSchedule = findViewById(R.id.navSchedule);
        navSettings = findViewById(R.id.navSettings);

        findViewById(R.id.btnReport).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReportActivity.class));
        });

        findViewById(R.id.btnHonorSettings).setOnClickListener(v -> {
            openHonorAppLaunchSettings();
        });

        updateHeader();
        updateXP();
        checkSystemHealth();
        loadTodaySchedule();
        loadUpcomingReview();
        setupBottomNavigation();
        highlightTab(navHome);
    }

    private void updateHeader() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) tvGreeting.setText("Good Morning!");
        else if (hour < 18) tvGreeting.setText("Good Afternoon!");
        else tvGreeting.setText("Good Evening!");

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String username = prefs.getString("username", "Student");
        tvName.setText(username);

        String dateStr = new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(new Date());
        tvDate.setText(dateStr);
    }

    private void updateXP() {
        XPManager xpManager = new XPManager(this);
        tvLevel.setText("Lvl " + xpManager.getLevel());
        tvRank.setText(xpManager.getRank());
        progressXP.setProgress(xpManager.getProgressToNextLevel());
    }

    private void checkSystemHealth() {
        // 1. Notifications (Android 13+)
        boolean notificationsGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        // 2. Overlay (Always required for App Lock)
        boolean overlayGranted = Settings.canDrawOverlays(this);

        // 3. Usage Stats (Always required for App Lock)
        boolean usageGranted = hasUsageStatsPermission();

        // 4. Exact Alarms (Android 12+)
        boolean alarmsGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmsGranted = am != null && am.canScheduleExactAlarms();
        }

        // 5. Battery Optimization
        boolean batteryIgnored = true;
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            batteryIgnored = pm.isIgnoringBatteryOptimizations(getPackageName());
        }

        final boolean finalNotificationsGranted = notificationsGranted;
        final boolean finalOverlayGranted = overlayGranted;
        final boolean finalUsageGranted = usageGranted;
        final boolean finalAlarmsGranted = alarmsGranted;
        final boolean finalBatteryIgnored = batteryIgnored;

        if (!notificationsGranted || !overlayGranted || !usageGranted || !alarmsGranted || !batteryIgnored) {
            layoutPermissionBanner.setVisibility(View.VISIBLE);

            String honorBrand = Build.MANUFACTURER.toLowerCase();
            if (honorBrand.contains("honor") || honorBrand.contains("huawei")) {
                tvPermissionMessage.setText("Needs permission for the app to work.");
            } else if (!usageGranted) {
                tvPermissionMessage.setText("Usage Access permission required!");
            } else if (!overlayGranted) {
                tvPermissionMessage.setText("Overlay permission required!");
            } else if (!notificationsGranted) {
                tvPermissionMessage.setText("Notification permission required!");
            } else if (!alarmsGranted) {
                tvPermissionMessage.setText("Exact Alarm permission required!");
            } else {
                tvPermissionMessage.setText("Ignore Battery Optimization!");
            }

            btnFixPermissions.setOnClickListener(v -> {
                if (!finalNotificationsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                } else if (!finalOverlayGranted) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                } else if (!finalUsageGranted) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                } else if (!finalAlarmsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                } else if (!finalBatteryIgnored) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            });
        } else {
            layoutPermissionBanner.setVisibility(View.GONE);
        }
    }

    public void openHonorAppLaunchSettings() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new android.content.ComponentName(
                    "com.hihonor.systemmanager",
                    "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
            ));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private boolean hasUsageStatsPermission() {
        try {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), getPackageName());
            } else {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), getPackageName());
            }
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkSystemHealth();
        }
    }

    private void loadTodaySchedule() {
        String today = new SimpleDateFormat("EEEE", Locale.US).format(new Date());
        ArrayList<Schedule> schedules = databaseHelper.getSchedulesByDay(today);

        if (schedules.isEmpty()) {
            tvSubject.setText("No classes today");
            tvTime.setText("Enjoy your free time!");
        } else {
            Schedule s = schedules.get(0);
            tvSubject.setText(s.getSubject());
            tvTime.setText(s.getStartTime() + " - " + s.getEndTime());
        }
    }

    private void loadUpcomingReview() {
        ArrayList<Review> reviews = databaseHelper.getAllReviews();
        if (reviews.isEmpty()) {
            tvReviewSubject.setText("No upcoming reviews");
            tvReviewTime.setText("Relax and recharge!");
        } else {
            Collections.sort(reviews, (r1, r2) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);
                try {
                    Date d1 = sdf.parse(r1.getDate() + " " + r1.getTime());
                    Date d2 = sdf.parse(r2.getDate() + " " + r2.getTime());
                    if (d1 == null || d2 == null) return 0;
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return 0;
                }
            });

            Review next = null;
            long now = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);

            for (Review r : reviews) {
                try {
                    Date d = sdf.parse(r.getDate() + " " + r.getTime());
                    if (d != null && d.getTime() > now) {
                        next = r;
                        break;
                    }
                } catch (ParseException ignored) {}
            }

            if (next != null) {
                tvReviewSubject.setText(next.getTopic());
                tvReviewTime.setText(next.getDate() + " • " + next.getTime());
            } else {
                tvReviewSubject.setText("No upcoming reviews");
                tvReviewTime.setText("All caught up!");
            }
        }
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {});
        navSubjects.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubjectsActivity.class));
        });
        navCalendar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
        });
        navSchedule.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
        });
        navSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        updateHeader();
        updateXP();
        checkSystemHealth();
        loadTodaySchedule();
        loadUpcomingReview();
    }
}