package com.example.studreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {

            Executors.newSingleThreadExecutor().execute(() -> {
                DatabaseHelper db = new DatabaseHelper(context);
                ArrayList<Review> reviews = db.getAllReviews();

                for (Review review : reviews) {
                    rearmDualAlarms(context, review);
                }
            });
        }
    }

    private void rearmDualAlarms(Context context, Review review) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);
        try {
            Date reviewDate = sdf.parse(review.getDate() + " " + review.getTime());
            if (reviewDate == null) return;

            long reviewTimeMillis = reviewDate.getTime();
            long reminderInterval = 0;
            String reminder = review.getReminder();

            if (reminder.contains("5 minutes")) reminderInterval = 5 * 60 * 1000;
            else if (reminder.contains("15 minutes")) reminderInterval = 15 * 60 * 1000;
            else if (reminder.contains("30 minutes")) reminderInterval = 30 * 60 * 1000;
            else if (reminder.contains("1 hour")) reminderInterval = 60 * 60 * 1000;

            long earlyTime = reviewTimeMillis - reminderInterval;

            // 1. Rearm Early Warning
            if (earlyTime > System.currentTimeMillis()) {
                int earlyRequestCode = (review.getId() + "_early").hashCode();
                setAlarm(context, earlyTime, earlyRequestCode, review, false);
            }

            // 2. Rearm Final Alarm
            if (reviewTimeMillis > System.currentTimeMillis()) {
                int finalRequestCode = (review.getId() + "_final").hashCode();
                setAlarm(context, reviewTimeMillis, finalRequestCode, review, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAlarm(Context context, long triggerTime, int requestCode, Review review, boolean isFinal) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("subject", review.getSubject());
        intent.putExtra("topic", review.getTopic());
        intent.putExtra("reviewId", review.getId());
        intent.putExtra("isFinalReminder", isFinal);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            Intent showIntent = new Intent(context, MainActivity.class);
            PendingIntent showPendingIntent = PendingIntent.getActivity(context, requestCode, showIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
            }
        }
    }
}