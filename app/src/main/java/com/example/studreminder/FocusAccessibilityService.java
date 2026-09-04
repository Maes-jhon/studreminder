package com.example.studreminder;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class FocusAccessibilityService extends AccessibilityService {

    private static final String TAG = "FocusService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            CharSequence packageName = event.getPackageName();
            if (packageName != null) {
                String appPackage = packageName.toString();

                if (appPackage.equals(getPackageName())) return;

                boolean active = FocusState.isSessionActive(this);
                boolean blocked = FocusState.isAppBlocked(this, appPackage);
                boolean alreadyBlocking = FocusState.isBlockingActive(this);

                Log.d(TAG, "Package Opened: " + appPackage + " | Active: " + active + " | Blocked: " + blocked + " | AlreadyBlocking: " + alreadyBlocking);

                if (blocked && !alreadyBlocking) {
                    // Check for overlay permission on Android 6.0+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        Log.e(TAG, "Overlay permission not granted. Cannot display blocker.");
                        return;
                    }

                    Intent intent = new Intent(this, FocusBlockerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {}
}