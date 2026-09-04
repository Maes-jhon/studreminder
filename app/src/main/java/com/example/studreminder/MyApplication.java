package com.example.studreminder;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {

    public static final String REMINDER_CHANNEL_ID = "review_reminder_channel";
    public static final String FOCUS_CHANNEL_ID = "focus_mode_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Reminder Channel
            NotificationChannel reminderChannel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Review Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            reminderChannel.setDescription("Notifications for scheduled study sessions");
            reminderChannel.enableVibration(true);
            reminderChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Focus Mode Channel
            NotificationChannel focusChannel = new NotificationChannel(
                    FOCUS_CHANNEL_ID,
                    "Focus Mode Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            focusChannel.setDescription("Status of the active study session");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(reminderChannel);
                manager.createNotificationChannel(focusChannel);
            }
        }
    }
}
