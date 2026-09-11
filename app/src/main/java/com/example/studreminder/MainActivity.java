package com.example.studreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navSubjects, navCalendar, navSchedule, navSettings;
    private TextView tvGreeting, tvDate;
    private TextView tvStatName, tvStatRank, tvStatLevel, tvXPText;
    private TextView pillSchool, pillCourse, pillYear;
    private LinearProgressIndicator progressStatXP;
    
    private RecyclerView recyclerTodosMain;
    private TodoAdapter todoAdapter;
    private ArrayList<Todo> todoList;
    
    private View layoutPermissionBanner;
    private TextView tvPermissionMessage;
    private Button btnFixPermissions;

    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private TextView tvTodoFilter;
    private String currentFilter = "Ongoing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        currentUsername = session.getString("username", "Student");

        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);

        tvStatName = findViewById(R.id.tvStatName);
        tvStatRank = findViewById(R.id.tvStatRank);
        tvStatLevel = findViewById(R.id.tvStatLevel);
        tvXPText = findViewById(R.id.tvXPText);
        progressStatXP = findViewById(R.id.progressStatXP);
        
        pillSchool = findViewById(R.id.pillSchool);
        pillCourse = findViewById(R.id.pillCourse);
        pillYear = findViewById(R.id.pillYear);

        findViewById(R.id.cardStat).setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        recyclerTodosMain = findViewById(R.id.recyclerTodosMain);
        recyclerTodosMain.setLayoutManager(new LinearLayoutManager(this));

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

        tvTodoFilter = findViewById(R.id.tvTodoFilter);
        ((View)tvTodoFilter.getParent()).setOnClickListener(v -> showFilterMenu());

        findViewById(R.id.btnAddTodoQuick).setOnClickListener(v -> {
            AddTodoBottomSheet bottomSheet = AddTodoBottomSheet.newInstance(-1); // -1 for global todos
            bottomSheet.setOnTodoAddedListener(this::loadTodos);
            bottomSheet.show(getSupportFragmentManager(), "ADD_TODO");
        });

        updateHeader();
        loadUserProfile();
        updateXP();
        loadTodos();
        checkSystemHealth();
        loadTodaySchedule();
        loadUpcomingReview();
        setupBottomNavigation();
        highlightTab(navHome);
    }

    private void updateHeader() {
        tvGreeting.setText("Hello, " + currentUsername);

        String dateStr = new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(new Date());
        tvDate.setText(dateStr);
    }

    private void loadUserProfile() {
        android.database.Cursor cursor = databaseHelper.getUserProfile(currentUsername);
        if (cursor != null && cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME));
            String school = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SCHOOL));
            String course = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURSE));
            String yearLevel = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_YEAR_LEVEL));
            
            if (fullName != null && !fullName.isEmpty()) tvStatName.setText(fullName.toUpperCase());
            else tvStatName.setText(currentUsername.toUpperCase());
            
            if (school != null && !school.isEmpty()) pillSchool.setText(school.toUpperCase());
            else pillSchool.setText("TCU");

            if (course != null && !course.isEmpty()) pillCourse.setText(course.toUpperCase());
            else pillCourse.setText("BSIS");

            if (yearLevel != null && !yearLevel.isEmpty()) pillYear.setText(yearLevel.toUpperCase());
            else pillYear.setText("2ND YEAR");
            
            cursor.close();
        } else {
            tvStatName.setText(currentUsername.toUpperCase());
            pillSchool.setText("TCU");
            pillCourse.setText("BSIS");
            pillYear.setText("2ND YEAR");
        }
    }

    private void updateXP() {
        XPManager xpManager = new XPManager(this);
        tvStatLevel.setText(String.valueOf(xpManager.getLevel()));
        tvStatRank.setText(xpManager.getRank());
        progressStatXP.setProgress(xpManager.getProgressToNextLevel());
        tvXPText.setText(xpManager.getProgressToNextLevel() + " / 100 XP");
    }

    private void showFilterMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvTodoFilter);
        popup.getMenu().add("Ongoing");
        popup.getMenu().add("Missed");
        popup.getMenu().add("Completed");

        popup.setOnMenuItemClickListener(item -> {
            currentFilter = item.getTitle().toString();
            tvTodoFilter.setText(currentFilter);
            loadTodos();
            return true;
        });
        popup.show();
    }

    private void loadTodos() {
        ArrayList<Review> reviews = databaseHelper.getAllReviews();
        todoList = new ArrayList<>();
        
        // Add global todos
        todoList.addAll(databaseHelper.getTodosByReviewId(-1));
        
        // Add todos from reviews
        for (Review r : reviews) {
            todoList.addAll(databaseHelper.getTodosByReviewId(r.getId()));
        }

        // Apply Filter
        ArrayList<Todo> filteredList = new ArrayList<>();
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (Todo t : todoList) {
            boolean isMissed = false;
            if (!t.isCompleted() && t.getDeadline() != null && !t.getDeadline().isEmpty()) {
                try {
                    java.util.Date d = sdf.parse(t.getDeadline());
                    if (d != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        if (now > cal.getTimeInMillis()) isMissed = true;
                    }
                } catch (Exception ignored) {}
            }

            if (currentFilter.equals("Completed") && t.isCompleted()) {
                filteredList.add(t);
            } else if (currentFilter.equals("Missed") && isMissed && !t.isCompleted()) {
                filteredList.add(t);
            } else if (currentFilter.equals("Ongoing") && !t.isCompleted() && !isMissed) {
                filteredList.add(t);
            }
        }
        
        todoAdapter = new TodoAdapter(filteredList, this::showTodoDetailsDialog);
        recyclerTodosMain.setAdapter(todoAdapter);
    }

    private void showTodoDetailsDialog(Todo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_todo_details, null);
        builder.setView(dialogView);

        TextView tvDiagSubject = dialogView.findViewById(R.id.tvDialogSubject);
        TextView tvDiagDate = dialogView.findViewById(R.id.tvDialogDate);
        TextView tvDiagTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDiagDesc = dialogView.findViewById(R.id.tvDialogDesc);
        TextView tvDiagSub = dialogView.findViewById(R.id.tvDialogSub);
        
        Button btnEdit = dialogView.findViewById(R.id.btnDialogEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDelete);

        tvDiagSubject.setText(todo.getLabel()); 
        tvDiagDate.setText(todo.getDeadline() != null && !todo.getDeadline().isEmpty() ? todo.getDeadline() : "No Deadline");
        tvDiagTitle.setText(todo.getTask());
        tvDiagDesc.setText(todo.getDescription() != null && !todo.getDescription().isEmpty() ? todo.getDescription() : "No description");
        
        if (todo.getSubTodos() != null && !todo.getSubTodos().isEmpty()) {
            tvDiagSub.setText(todo.getSubTodos());
        } else {
            dialogView.findViewById(R.id.tvDialogSubLabel).setVisibility(View.GONE);
            tvDiagSub.setVisibility(View.GONE);
        }

        AlertDialog dialog = builder.create();
        
        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit coming soon", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            databaseHelper.deleteTodo(todo.getId());
            loadTodos();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showPromotionDialog(String newRank) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_promotion_dialog, null);
        builder.setView(dialogView);

        TextView tvNewRank = dialogView.findViewById(R.id.tvNewRank);
        TextView tvRewardText = dialogView.findViewById(R.id.tvRewardText);
        Button btnClaim = dialogView.findViewById(R.id.btnPromotionClaim);

        tvNewRank.setText(newRank);
        XPManager xp = new XPManager(this);
        tvRewardText.setText("Reward: " + xp.getRankReward(newRank));

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        btnClaim.setOnClickListener(v -> {
            updateXP();
            loadUserProfile(); // Refresh pills and stat card
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkSystemHealth() {
        boolean notificationsGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        boolean overlayGranted = Settings.canDrawOverlays(this);
        boolean usageGranted = hasUsageStatsPermission();

        boolean alarmsGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmsGranted = am != null && am.canScheduleExactAlarms();
        }

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

            if (!usageGranted) tvPermissionMessage.setText("Usage Access required!");
            else if (!overlayGranted) tvPermissionMessage.setText("Overlay required!");
            else if (!notificationsGranted) tvPermissionMessage.setText("Notification required!");
            else if (!alarmsGranted) tvPermissionMessage.setText("Exact Alarm required!");
            else tvPermissionMessage.setText("Ignore Battery Optimization!");
            
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

    private void openHonorAppLaunchSettings() {
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
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            if (mode != AppOpsManager.MODE_ALLOWED) return false;

            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<android.app.usage.UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time);
            return stats != null && !stats.isEmpty();
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
        
        if (!schedules.isEmpty()) {
            Schedule s = schedules.get(0);
            ((TextView)findViewById(R.id.tvSubject)).setText(s.getSubject());
            ((TextView)findViewById(R.id.tvTime)).setText(s.getStartTime() + " - " + s.getEndTime());
        }
    }

    private void loadUpcomingReview() {
        ArrayList<Review> reviews = databaseHelper.getAllReviews();
        if (!reviews.isEmpty()) {
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
                ((TextView)findViewById(R.id.tvReviewSubject)).setText(next.getTopic());
                ((TextView)findViewById(R.id.tvReviewTime)).setText(next.getDate() + " • " + next.getTime());
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
        loadUserProfile();
        updateXP();
        loadTodos();
        checkSystemHealth();
        loadTodaySchedule();
        loadUpcomingReview();
    }
}
