# Remindly: Comprehensive Technical Code Documentation

This guide provides a detailed technical breakdown of every class and resource in the **Remindly** project. It is organized by architectural layer to explain how the components interact to form a cohesive student productivity ecosystem.

---

## 1. Core Infrastructure & Data Layer
These files form the foundation of the app, handling global state, persistence, and data modeling.

*   **`MyApplication.java`**: The global application class. It initializes application-wide components and ensures the app context is available for background services.
*   **`DatabaseHelper.java`**: The central SQL engine. It manages the SQLite database schema, table creation, and all CRUD (Create, Read, Update, Delete) operations. It includes high-level logic for filtering archived items and performing database migrations using the `onUpgrade` method.
*   **`XPManager.java`**: Handles the gamification logic. It stores user experience points in `SharedPreferences` and calculates the user's level and rank (e.g., "Novice Scholar") based on task completion.
*   **`FocusState.java`**: A state-management utility that keeps track of active "Study Missions." it uses `SharedPreferences` to persist the current blocklist of apps and the status of the app locker.

### **Data Models**
*   **`Subject.java`**: Represents a course (Name, Teacher, Room, Color).
*   **`Schedule.java`**: Represents a recurring class time, including start/end times and notification offsets.
*   **`Review.java`**: Represents a dedicated study session linked to a subject.
*   **`Todo.java`**: Represents a specific task with a description, sub-tasks, and a state-managed status (`Pending`, `In Progress`, `Completed`, `Missed`).
*   **`ReviewFile.java`, `ReviewLink.java`, `ReviewNote.java`, `ReviewStudySet.java`**: specialized models for storing resources attached to study sessions.
*   **`AppModel.java`**: Represents an installed Android application (Icon, Label, Package Name) for the blocklist selector.
*   **`ArchivedItemModel.java`**: A generic wrapper used to display any archived entity (Subject, Review, etc.) in the archive management screen.
*   **`PerformanceData.java`**: A data transfer object used to pass statistics from the database to the **Report** screen.

---

## 2. Main Feature Activities
The primary user interfaces where data is created and managed.

*   **`MainActivity.java`**: The main dashboard. It dynamically updates the user greeting, rank, and "Today's Mission" (the next upcoming class or review).
*   **`SubjectsActivity.java` & `SubjectDetailsActivity.java`**: Manage the hierarchy of subjects. Users can see an overview of their courses or dive into a specific subject's folder.
*   **`ScheduleActivity.java`**: Features a 7-day selector logic that filters the SQLite database to show classes for a specific day.
*   **`CalendarActivity.java`**: An advanced date-picker interface that allows users to select any day in a month-view and view the corresponding daily agenda.
*   **`ReviewDetailsActivity.java`**: The resource hub. It uses a `TabLayout` to switch between To-dos, Notes, Files, and Links for a specific session.
*   **`ArchivedItemsActivity.java`**: The management screen for hidden items. It allows users to "Unarchive" data (flip a bit in SQLite) or permanently delete records.
*   **`AddSubjectActivity.java`, `AddScheduleActivity.java`, `AddReviewActivity.java`**: Specialized forms that support both **Insert** (New) and **Update** (Edit) modes by detecting Intent extras.

---

## 3. Adapters (The UI Logic Layer)
Adapters bridge the gap between the database models and the visual RecyclerView lists.

*   **`SubjectAdapter.java`, `ScheduleAdapter.java`, `ReviewAdapter.java`**: Handle the rendering of high-level cards. They include the logic for long-press menus (Edit/Delete) and one-tap Archiving.
*   **`TodoAdapter.java`**: Contains the "Smart Status" logic. It calculates the `Missed` status by comparing timestamps and manages the 3-state toggle for task progress.
*   **`ArchivedItemsAdapter.java`**: Specifically designed to handle restoration logic for various data types within a single list.
*   **`AppAdapter.java`**: Handles the list of installed applications, allowing users to toggle checkboxes to build their focus blocklist.
*   **`ReviewNoteAdapter.java`, `ReviewFileAdapter.java`, `ReviewLinkAdapter.java`, `ReviewStudySetAdapter.java`**: Render specific study resources with appropriate actions (Open Link, Open PDF, etc.).

---

## 4. Services & Background Logic (The Focus Layer)
These components work outside the main UI to manage notifications and block distractions.

*   **`FocusAccessibilityService.java`**: A low-level system observer. It listens for `TYPE_WINDOW_STATE_CHANGED` events from the Android system to detect which app the user is currently using.
*   **`AppLockerService.java`**: A foreground service that manages the focus mission. It coordinates with the Accessibility Service to determine if the `FocusBlockerActivity` should be displayed.
*   **`FocusBlockerActivity.java`**: The "Study Mission" overlay. It uses `WindowManager` flags to appear on top of other apps, providing a physical barrier against distraction.
*   **`ReminderReceiver.java`**: A `BroadcastReceiver` that catches alarms from the system. It builds and displays high-priority notifications for classes and reviews.
*   **`BootReceiver.java`**: Ensures that all scheduled study reminders and app-locking services are rescheduled immediately if the phone is restarted.
*   **`WordImporter.java`**: A utility that parses `.docx` files using the Apache POI or similar logic to convert documents into study notes.

---

## 5. UI Resources (The Aesthetic Layer)
The definition of the "So Matcha" Neo-brutalist brand.

*   **`themes.xml` & `values-night/themes.xml`**: Define the global color palette (Ivory #FBFCEE, Tea Green #C8D69B, Celtic Blue #3971B8) and force the high-contrast 2dp black borders on all system components.
*   **`selector_brutal_text.xml`**: A color state list that ensures text remains readable and switches colors when buttons are pressed.
*   **Layouts (`activity_*.xml`, `item_*.xml`, `dialog_*.xml`)**:
    *   `activity_main.xml`: The complex dashboard layout.
    *   `item_todo.xml`: The smart task card with status badges.
    *   `layout_lock_overlay.xml`: The design for the "Mission Failed" focus screen.
*   **`accessibility_service_config.xml`**: Configures the focus tracker to only listen for app-switching events, preserving battery and user privacy.

---

> [!IMPORTANT]
> **Technical Highlight for Professor**: "The application follows a **Decoupled Architecture**. The UI (Activities) never talks directly to the raw data; it always uses **Adapters** for rendering and the **DatabaseHelper** for persistence, ensuring the app is stable, scalable, and easy to debug."
