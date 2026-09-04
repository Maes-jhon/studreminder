package com.example.studreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID_V2 = "STUDY_REMINDER_CHANNEL_V2";

    @Override
    public void onReceive(Context context, Intent intent) {
        String topic = intent.getStringExtra("topic");
        String subject = intent.getStringExtra("subject");
        String room = intent.getStringExtra("room");
        int reviewId = intent.getIntExtra("reviewId", -1);
        boolean focusMode = intent.getBooleanExtra("focusMode", false);
        boolean isFinal = intent.getBooleanExtra("isFinalReminder", false);
        boolean isClass = intent.getBooleanExtra("isClassReminder", false);

        if (topic == null) topic = "Study Session";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_V2,
                    "Remindly Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent resultIntent;
        if (isClass) {
            resultIntent = new Intent(context, ScheduleActivity.class);
        } else {
            resultIntent = new Intent(context, ReviewDetailsActivity.class);
            resultIntent.putExtra("reviewId", reviewId);
            resultIntent.putExtra("subject", subject);
            resultIntent.putExtra("topic", topic);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                isClass ? (int) System.currentTimeMillis() : reviewId,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_V2)
                .setSmallIcon(R.drawable.ic_remindly_logo)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (isClass) {
            if (isFinal) {
                builder.setContentTitle("Class Started! 🚀")
                       .setContentText("Your " + subject + " class has started in " + room + ".")
                       .setPriority(NotificationCompat.PRIORITY_MAX);
            } else {
                builder.setContentTitle("Class Starting Soon! 🏫")
                       .setContentText("Your " + subject + " class in " + room + " starts soon.")
                       .setPriority(NotificationCompat.PRIORITY_HIGH);
            }
        } else {
            if (isFinal) {
                builder.setContentTitle("Mission Started! 🚀")
                       .setContentText("Time to review " + topic + ". Stay focused!")
                       .setPriority(NotificationCompat.PRIORITY_MAX)
                       .setFullScreenIntent(pendingIntent, true);

                if (focusMode) {
                    Intent serviceIntent = new Intent(context, AppLockerService.class);
                    serviceIntent.putExtra("reviewId", reviewId);
                    ContextCompat.startForegroundService(context, serviceIntent);
                }
            } else {
                builder.setContentTitle("Get Ready! 📚")
                       .setContentText("Your study mission for " + topic + " starts soon.")
                       .setPriority(NotificationCompat.PRIORITY_HIGH);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
