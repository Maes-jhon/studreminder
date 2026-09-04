# Remindly: Technical Code Explanation Guide

Use this guide during the technical portion of your presentation to explain *how* the app works under the hood.

---

## **1. The Database (The Persistence Layer)**
### **File: `DatabaseHelper.java`**
*   **What it is**: The "brain" that remembers everything. It uses **SQLite**, a lightweight database stored directly on the phone.
*   **Key Concept: Versioning & Migration**:
    *   "We use a `DATABASE_VERSION` system. When we added new features like the 'Archive' or 'Double Notifications', we didn't delete the user's data. We used `onUpgrade` with **`ALTER TABLE`** commands to surgically add new columns to the existing tables."
*   **Key Logic**:
    *   **Archive Filter**: Every `SELECT` query in the app automatically includes `WHERE is_archived = 0`. This is why items "vanish" from the list but remain safe in the database.

---

## **2. The App Locker (Focus Engineering)**
### **Files: `FocusAccessibilityService.java` & `AppLockerService.java`**
*   **How it works**: This is the most advanced part of the app.
*   **Accessibility Service**: "This service acts as an observer. It 'listens' to which app the user is currently looking at. If the app package name (like `com.zhiliaoapp.musically` for TikTok) matches our blocklist, it triggers the lock."
*   **WindowManager Overlay**: "Instead of just closing the app, we use a **WindowManager overlay**. We draw our 'Study Mission' screen directly on top of every other app. Because it has a higher 'layer priority' in Android, the user cannot click anything behind it until the mission ends."

---

## **3. Smart To-dos (The Logic Engine)**
### **File: `TodoAdapter.java`**
*   **Automation: The "Auto-Missed" Logic**:
    *   "We don't need a background timer to check for missed deadlines. Every time the list is loaded, the code compares the `currentDate` with the `deadlineDate`. If Today > Deadline and the task isn't 'Completed', the UI dynamically renders it as **Missed** in red."
*   **Interaction: Status Cycling**:
    *   "We implemented a 'State Machine' for the status badge. Clicking it triggers a simple rotation: `Pending` -> `In Progress` -> `Completed`. This updates the SQLite database in real-time."

---

## **4. Double Notification System (Alarms)**
### **Files: `AddScheduleActivity.java` & `ReminderReceiver.java`**
*   **The "Double Alarm" Trick**:
    *   "To ensure reliability, we use Android's **`AlarmManager`**. When a class is saved, we don't just set one alarm; we set two."
    *   **Request Codes**: "We give the first alarm an ID (e.g., `105`) and the second alarm an ID + 10,000 (e.g., `10105`). This prevents the second alarm from overwriting the first one, allowing both notifications to fire perfectly."
*   **PendingIntents**: "These are like 'time-traveling letters'. We give them to the Android System, and even if Remindly is completely closed, the System will open our `ReminderReceiver` when the time is right."

---

## **5. UI Architecture (Neo-Brutalism)**
### **File: `themes.xml` & Layout XMLs**
*   **Design Implementation**:
    *   "To achieve the **Neo-brutalist** look, we avoided standard Material Design shadows. Instead, we created custom drawables with **solid black strokes (2dp)** and used **`translationZ`** or hard-coded offsets to create 'block shadows' that don't blur."
    *   **Paper Style**: "We used a custom `ThemeOverlay` for our pickers to force the 'So Matcha' ivory background, ensuring the Android system dialogs match our app's unique brand."

---

> [!TIP]
> **Summary for Judges**: "Our code architecture focuses on three things: **Data Integrity** (Safe migrations), **Active Focus** (Overlay-based locking), and **User Motivation** (Real-time XP updates and smart status tracking)."
