# Walkthrough - Double Notification System for Class Schedule

I have implemented a high-reliability "Double Notification" system for the Class Schedule, similar to Google Calendar but optimized for students.

## Changes Made

### 1. Database Upgrade (DatabaseHelper.java)
- **Safe Migration**: Bumped the database to Version 16 and used `ALTER TABLE` to add the `reminder_offset` column. Your existing classes are safe and haven't been deleted.
- **Model Update**: Updated the `Schedule` object to carry the reminder setting throughout the app.

### 2. UI Enhancement (activity_add_schedule.xml)
- **Reminder Dropdown**: Added a new "Reminder" field in the Add/Edit Schedule screen.
- **Options**: You can now choose to be notified **5m, 15m, 30m, or 1h** before a class begins.

### 3. Double-Alarm Logic (AddScheduleActivity.java)
- **Automatic Multi-Scheduling**: Whenever you save a class, the app now sets **two separate alarms**:
    - **Alarm 1 (Early)**: Triggers at your chosen time (e.g., 15 mins before).
    - **Alarm 2 (On-Time)**: Triggers exactly when the class starts.
- **Conflict Prevention**: I used unique request codes (`scheduleId` and `scheduleId + 10000`) so the two alarms never overwrite each other.

### 4. Smart Notifications (ReminderReceiver.java)
- **Dynamic Titles**:
    - **Early**: "Class Starting Soon! 🏫"
    - **On-Time**: "Class Started! 🚀"
- **Contextual Info**: The notification now includes the **Subject** and **Room Number**, so you can head straight to class without opening the app.

## Verification Results

### Build Status
- **Success**: The project builds successfully with no errors.

### Manual Verification Path
1. **Add a Class**: Open "Add Schedule," fill in your subject and room, and set a 5-minute reminder.
2. **Early Hit**: Set the start time to 6 minutes from now. You should receive a "Class Starting Soon!" notification in 1 minute.
3. **On-Time Hit**: Wait 5 more minutes. You should receive a second "Class Started!" notification exactly as the class begins.
4. **Edit Test**: Edit the class and change the reminder. The app will automatically update the alarms to the new time.

> [!TIP]
> Make sure to grant the "Exact Alarm" permission if prompted on Android 12+, otherwise the notifications might be delayed by the system battery saver.
