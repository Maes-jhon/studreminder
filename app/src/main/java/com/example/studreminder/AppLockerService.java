package com.example.studreminder;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class AppLockerService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    private boolean isOverlayVisible = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable monitorRunnable;

    private boolean isBreakTime = false;
    private long startTime;
    private static final long STUDY_DURATION = 25 * 60 * 1000;
    private static final long BREAK_DURATION = 5 * 60 * 1000;

    private SharedPreferences lockerPrefs;

    private final String[] quotes = {
            "The expert in anything was once a beginner.",
            "Study now, be proud later.",
            "Don't stop until you're proud.",
            "Focus on the goal, not the obstacle.",
            "Your future self will thank you."
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        lockerPrefs = getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);
        startTime = System.currentTimeMillis();
        startForeground(999, getFocusNotification(
                getString(R.string.focus_mode_notification_title),
                getString(R.string.focus_mode_notification_start)
        ));
        startMonitoring();
    }

    private void startMonitoring() {
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                updatePomodoroState();
                if (!isBreakTime) {
                    checkForegroundApp();
                } else {
                    hideOverlay();
                }
                handler.postDelayed(this, 800);
            }
        };
        handler.post(monitorRunnable);
    }

    private void updatePomodoroState() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (!isBreakTime && elapsed >= STUDY_DURATION) {
            isBreakTime = true;
            startTime = System.currentTimeMillis();
            updateNotification(
                    getString(R.string.focus_mode_break_title),
                    getString(R.string.focus_mode_break_content)
            );
        } else if (isBreakTime && elapsed >= BREAK_DURATION) {
            isBreakTime = false;
            startTime = System.currentTimeMillis();
            updateNotification(
                    getString(R.string.focus_mode_back_title),
                    getString(R.string.focus_mode_back_content)
            );
        }
    }

    private void checkForegroundApp() {
        if (!hasUsageStatsPermission()) {
            return;
        }

        String currentApp = getForegroundPackageName();
        if (currentApp == null || currentApp.isEmpty()) return;

        Set<String> lockedApps = lockerPrefs.getStringSet("LOCKED_APPS_SET", new HashSet<>());

        if ((lockedApps.contains(currentApp) || isBroadlyBlocked(currentApp))
                && !currentApp.equals(getPackageName())) {
            showOverlay();
        } else {
            hideOverlay();
        }
    }

    private boolean isBroadlyBlocked(String pkg) {
        if (pkg == null || pkg.isEmpty()) return false;
        Set<String> lockedApps = lockerPrefs.getStringSet("LOCKED_APPS_SET", new HashSet<>());

        if (lockedApps.contains(pkg)) return true;

        boolean isFacebookPackage = pkg.contains("facebook") || pkg.contains("katana") || pkg.contains("orca");
        if (isFacebookPackage) {
            for (String locked : lockedApps) {
                if (locked.contains("facebook") || locked.contains("katana") || locked.contains("orca")) {
                    return true;
                }
            }
        }

        if (pkg.contains("tiktok")) {
            for (String locked : lockedApps) {
                if (locked.contains("tiktok")) return true;
            }
        }
        return false;
    }

    private String getForegroundPackageName() {
        String foregroundApp = "";
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        UsageEvents usageEvents = usm.queryEvents(time - 3000, time);
        UsageEvents.Event event = new UsageEvents.Event();

        while (usageEvents != null && usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                foregroundApp = event.getPackageName();
            }
        }
        return foregroundApp;
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

    private void showOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            return;
        }

        if (isOverlayVisible) {
            updateTimerUI();
            return;
        }

        WindowManager.LayoutParams params = createOverlayLayoutParams();

        // Resolves the MaterialButton crash by attaching a Material theme context wrapper
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, com.google.android.material.R.style.Theme_MaterialComponents_DayNight_NoActionBar);
        overlayView = LayoutInflater.from(wrapper).inflate(R.layout.layout_lock_overlay, null);

        TextView tvQuote = overlayView.findViewById(R.id.tvQuote);
        if (tvQuote != null) {
            tvQuote.setText(quotes[new Random().nextInt(quotes.length)]);
        }

        // Overlay Button Actions
        MaterialButton btnSubmitPin = overlayView.findViewById(R.id.btnSubmitPin);
        MaterialButton btnUnlock = overlayView.findViewById(R.id.btnUnlock);
        LinearLayout pinContainer = overlayView.findViewById(R.id.pinContainer);
        EditText etPinInput = overlayView.findViewById(R.id.etPinInput);

        if (btnUnlock != null && pinContainer != null) {
            btnUnlock.setOnClickListener(v -> pinContainer.setVisibility(View.VISIBLE));
        }

        TextView btnEmergency = overlayView.findViewById(R.id.btnEmergency);
        if (btnEmergency != null && pinContainer != null) {
            btnEmergency.setOnClickListener(v -> pinContainer.setVisibility(View.VISIBLE));
        }

        if (btnSubmitPin != null && etPinInput != null) {
            btnSubmitPin.setOnClickListener(v -> {
                String enteredPin = etPinInput.getText().toString();
                String savedPin = lockerPrefs.getString("EMERGENCY_PIN", "1234");
                if (enteredPin.equals(savedPin)) {
                    FocusState.setBlockingActive(this, false);
                    stopSelf();
                } else {
                    Toast.makeText(this, R.string.toast_incorrect_pin, Toast.LENGTH_SHORT).show();
                    etPinInput.setText("");
                }
            });
        }

        windowManager.addView(overlayView, params);
        isOverlayVisible = true;
        FocusState.setBlockingActive(this, true);
        updateTimerUI();
    }

    @SuppressWarnings("deprecation")
    private WindowManager.LayoutParams createOverlayLayoutParams() {
        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        // Reduced flags to avoid IHwWindowManager permission issues on Honor/Huawei
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutType,
                flags,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        return params;
    }

    private void updateTimerUI() {
        if (overlayView == null) return;
        TextView tvTimer = overlayView.findViewById(R.id.tvTimer);
        if (tvTimer == null) return;

        long remaining = STUDY_DURATION - (System.currentTimeMillis() - startTime);
        if (remaining < 0) remaining = 0;
        int mins = (int) (remaining / 1000) / 60;
        int secs = (int) (remaining / 1000) % 60;
        tvTimer.setText(String.format(Locale.US, "%02d:%02d", mins, secs));
    }

    private void hideOverlay() {
        if (isOverlayVisible && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
            } catch (Exception ignored) {}
            overlayView = null;
            isOverlayVisible = false;
            FocusState.setBlockingActive(this, false);
        }
    }

    private Notification getFocusNotification(String title, String content) {
        return new NotificationCompat.Builder(this, MyApplication.FOCUS_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_remindly_logo)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String title, String content) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(999, getFocusNotification(title, content));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), AppLockerService.class);
        restartServiceIntent.setPackage(getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restartServiceIntent);
        } else {
            startService(restartServiceIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        hideOverlay();
        if (monitorRunnable != null) handler.removeCallbacks(monitorRunnable);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}