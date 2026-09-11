# Implementation Plan - Resolving Logcat Errors & System Noise

This plan addresses the fatal crash in the Settings screen and provides clarity on the non-critical system logs emitted by the Honor/Huawei operating system.

## User Review Required

> [!IMPORTANT]
> **Fatal Exception Resolved**: The `ClassCastException` in your logcat was caused by a mismatch between the XML layout and the Java code for the "Exit App" button. I have already applied the fix in `SettingsActivity.java`. If you still see this error, please ensure you have **re-built** the project.

> [!NOTE]
> **System Noise (Honor/Huawei)**: Most of the other errors in your logcat (like `IHwWindowManager`, `SWAP_GkiKernelInterface`, and `readAppMemcgInfo`) are **System Noise**. These are emitted by the phone's operating system (MagicOS) and are not caused by bugs in your app's code. They do not affect the app's performance or stability.

## Proposed Changes

### 1. Finalizing the Crash Fix
#### [MODIFY] [SettingsActivity.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/SettingsActivity.java)
- Double-check that all view variables match their XML types (`MaterialCardView` for menu items, `MaterialButton` for the Exit button).
- Ensure explicit casting is used for absolute safety during the `findViewById` process.

### 2. Reducing System Warnings (App Locker)
#### [MODIFY] [AppLockerService.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/AppLockerService.java)
- Clean up `WindowManager.LayoutParams` flags.
- Remove `FLAG_SHOW_WHEN_LOCKED` and `FLAG_LAYOUT_IN_SCREEN`. These legacy flags often trigger the `IHwWindowManager` permission check on Honor devices because they are restricted to system-level apps on newer OS versions.
- Using only `FLAG_NOT_TOUCH_MODAL` is sufficient for our focus overlay and is more "Honor-friendly."

### 3. Logcat Filtering Guide
I will provide instructions on how to use the **Logcat Filter** in Android Studio to hide system noise, so you can focus only on your app's actual output during the presentation.

## Verification Plan

### Automated Checks
- Run `gradle build` to ensure no type mismatches remain.

### Manual Verification
1.  **Crash Test**: Open Settings. Verify the screen opens and the app no longer crashes.
2.  **App Locker Test**: Start a study mission and open a blocked app. Verify the overlay still appears correctly with the cleaner flags.
3.  **Logcat Audit**: Monitor the logs. Verify that no `FATAL EXCEPTION` entries appear when navigating the app.
