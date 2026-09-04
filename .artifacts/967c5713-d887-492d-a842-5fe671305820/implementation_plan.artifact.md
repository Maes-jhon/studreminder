# Implementation Plan - Class Schedule Double Notifications

Add a high-reliability "Double Notification" system for the Class Schedule, triggering once at the selected interval (e.g., 15m before) and a second time exactly when the class starts.

## User Review Required

> [!IMPORTANT]
> **Double Alarm Logic**: For every class saved, the app will now schedule two distinct alarms. I will use unique request codes (e.g., `scheduleId` and `scheduleId + 10000`) to ensure Android doesn't overwrite the first alarm with the second.

> [!NOTE]
> **Safe Migration**: I will use `ALTER TABLE` in the database upgrade (Version 16) to add the `reminder_offset` column, ensuring your existing schedule data is preserved.

## Proposed Changes

### 1. Database Update
#### [MODIFY] [DatabaseHelper.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/DatabaseHelper.java)
- Increment `DATABASE_VERSION` to **16**.
- `onUpgrade`: Add `COL_SCHEDULE_REMINDER` (TEXT) to the `schedule` table.
- Update `insertSchedule` and `updateSchedule` to handle the new reminder offset field.

---

### 2. UI Enhancement (Add/Edit Schedule)
#### [MODIFY] [activity_add_schedule.xml](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/res/layout/activity_add_schedule.xml)
- Add a `TextInputLayout` with a dropdown (`MaterialAutoCompleteTextView`) for **Reminder Offset**.
- Options: `5 minutes before`, `15 minutes before`, `30 minutes before`, `1 hour before`.

#### [MODIFY] [AddScheduleActivity.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/AddScheduleActivity.java)
- Initialize and populate the Reminder dropdown.
- Implement `scheduleClassAlarms(int scheduleId, String subject, String date, String time, String offset)`:
    1. **Calculate Trigger 1**: Start Time minus the selected offset (Early Reminder).
    2. **Calculate Trigger 2**: Exactly at Start Time (On-Time Alert).
    3. **Set Alarms**: Use `AlarmManager.setAlarmClock` with unique request codes for both.

---

### 3. Notification Logic
#### [MODIFY] [ReminderReceiver.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/ReminderReceiver.java)
- Refine the on-receive logic to distinguish between "Early" and "Final/On-Time" class reminders.
- **Early Title**: "Class Starting Soon! 🏫"
- **On-Time Title**: "Class Started! 🚀"
- Include the room number in the notification content for quick reference.

## Verification Plan

### Manual Verification
1.  **Double Hit Test**: Create a class starting in 10 minutes with a 5-minute reminder.
    - Verify Notification 1 triggers at T-5 minutes.
    - Verify Notification 2 triggers at T-0 minutes.
2.  **Unique IDs**: Create two different classes at different times. Verify all 4 notifications (2 for each class) trigger correctly and don't overwrite each other.
3.  **Permission Check**: On Android 12+, verify the app prompts for "Exact Alarm" permission if it's missing.
