package com.example.studreminder;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class FocusState {
    private static final String PREF_NAME = "locker_prefs";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_SUBJECT = "current_subject";
    private static final String KEY_LOCKED_APPS = "LOCKED_APPS_SET";
    private static final String KEY_IS_BLOCKING = "is_blocking_active";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void startSession(Context context, String subject, Set<String> blockedPackages) {
        getPrefs(context).edit()
                .putBoolean(KEY_IS_ACTIVE, true)
                .putString(KEY_SUBJECT, subject)
                .putStringSet(KEY_LOCKED_APPS, blockedPackages)
                .apply();
    }

    public static void endSession(Context context) {
        getPrefs(context).edit()
                .putBoolean(KEY_IS_ACTIVE, false)
                .putBoolean(KEY_IS_BLOCKING, false)
                .putString(KEY_SUBJECT, "")
                .apply();
    }

    public static boolean isSessionActive(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_ACTIVE, false);
    }

    public static void setBlockingActive(Context context, boolean active) {
        getPrefs(context).edit().putBoolean(KEY_IS_BLOCKING, active).apply();
    }

    public static boolean isBlockingActive(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_BLOCKING, false);
    }

    public static String getCurrentSubject(Context context) {
        return getPrefs(context).getString(KEY_SUBJECT, "Study Session");
    }

    // Updates the active package blocklist dynamically without requiring a session restart
    public static void updateSessionApps(Context context, Set<String> updatedPackages) {
        getPrefs(context).edit()
                .putStringSet(KEY_LOCKED_APPS, updatedPackages)
                .apply();
    }

    public static boolean isAppBlocked(Context context, String packageName) {
        if (!isSessionActive(context)) return false;
        Set<String> blockedApps = getPrefs(context).getStringSet(KEY_LOCKED_APPS, new HashSet<>());
        return blockedApps.contains(packageName);
    }
}