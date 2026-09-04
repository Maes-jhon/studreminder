# StudReminder Project Components Report

This document identifies and explains the purpose of all classes, variables, constants, and methods used in the **StudReminder** Android application.

---

## 1. Activities (UI Components)

### [AddReviewActivity](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/AddReviewActivity.java)
**Purpose:** Handles the creation and editing of review sessions for specific subjects.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `btnBack` | `ImageButton` | Navigates back to the previous screen. |
| **Variable** | `etSubject`, `etTopic`, `etDate`, `etTime` | `TextInputEditText` | User input fields for review details. |
| **Variable** | `dropdownReminder` | `MaterialAutoCompleteTextView` | Dropdown for selecting reminder intervals. |
| **Variable** | `databaseHelper` | `DatabaseHelper` | Interface for database operations. |
| **Variable** | `editMode` | `boolean` | Flags whether the activity is in edit mode or create mode. |
| **Method** | `onCreate()` | `void` | Initializes UI components and listeners. |
| **Method** | `setupReminderDropdown()` | `void` | Populates the reminder interval options. |
| **Method** | `loadActivityData()` | `void` | Loads existing review data when in edit mode. |
| **Method** | `saveOrUpdateReview()` | `void` | Validates input and saves/updates the review in the database. |
| **Method** | `showDatePicker()` | `void` | Displays a dialog for date selection. |
| **Method** | `showTimePicker()` | `void` | Displays a dialog for time selection. |

### [AddScheduleActivity](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/AddScheduleActivity.java)
**Purpose:** Allows users to add or edit class schedules.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `etSubject`, `etDate`, `etStartTime`, `etEndTime`, `etRoom` | `TextInputEditText` | Input fields for schedule details. |
| **Method** | `saveSchedule()` | `void` | Persists the schedule information to the local database. |

### [MainActivity](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/MainActivity.java)
**Purpose:** The entry point of the app, providing a dashboard with navigation to various features.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `navHome`, `navSubjects`, `navCalendar`, `navSchedule`, `navSettings` | `LinearLayout` | Navigation tabs for the bottom navigation bar. |
| **Method** | `onCreate()` | `void` | Sets up the main layout and navigation listeners. |

### [SubjectsActivity](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/SubjectsActivity.java)
**Purpose:** Displays a list of all subjects added by the user.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `recyclerSubjects` | `RecyclerView` | Displays the list of subjects efficiently. |
| **Variable** | `adapter` | `SubjectAdapter` | Manages the display of individual subject items. |
| **Method** | `loadSubjects()` | `void` | Fetches all subjects from the database and updates the list. |

---

## 2. Data Models (POJOs)

### [Subject](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Subject.java)
**Purpose:** Represents an academic subject entity.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `id` | `int` | Unique identifier for the subject. |
| **Variable** | `subject` | `String` | Name of the subject. |
| **Variable** | `teacher` | `String` | Name of the instructor. |
| **Variable** | `room` | `String` | Classroom location. |
| **Variable** | `color` | `int` | UI color associated with the subject. |

### [Schedule](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Schedule.java)
**Purpose:** Represents a specific class schedule.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `startTime`, `endTime` | `String` | Start and end times of the class. |
| **Method** | `getTime()` | `String` | Formats and returns the full time range string. |

### [Review](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Review.java)
**Purpose:** Represents a study or review session.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `topic` | `String` | The specific topic to be reviewed. |
| **Variable** | `reminder` | `String` | The reminder setting for the session. |

### [Todo](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Todo.java)
**Purpose:** Represents a task item within a review session.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `task` | `String` | The description of the task. |
| **Variable** | `completed` | `boolean` | Status of the task (done or not). |

---

## 3. Database Management

### [DatabaseHelper](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/DatabaseHelper.java)
**Purpose:** Manages the SQLite database, including table creation, versioning, and CRUD operations.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Constant** | `DATABASE_NAME` | `String` | Name of the SQLite file ("StudentReminder.db"). |
| **Constant** | `TABLE_SCHEDULE`, `TABLE_SUBJECT`, `TABLE_REVIEW`, `TABLE_TODO` | `String` | Table names for different data entities. |
| **Method** | `onCreate()` | `void` | Executes SQL commands to create all tables. |
| **Method** | `insertSubject()` | `boolean` | Adds a new subject to the database. |
| **Method** | `getAllSchedules()` | `ArrayList` | Retrieves all schedule records. |
| **Method** | `updateTodoCompleted()` | `boolean` | Updates the completion status of a To-Do item. |

---

## 4. Adapters (List Management)

### [SubjectAdapter](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/SubjectAdapter.java)
**Purpose:** Binds subject data to the RecyclerView items in `SubjectsActivity`.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Method** | `onCreateViewHolder()` | `ViewHolder` | Inflates the subject item layout. |
| **Method** | `onBindViewHolder()` | `void` | Sets data for each subject card (name, teacher, room). |

### [TodoAdapter](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/TodoAdapter.java)
**Purpose:** Binds To-Do items to the RecyclerView in `ReviewDetailsActivity`.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Method** | `updateTaskStyle()` | `void` | Updates the text style (strikethrough) based on completion status. |

---

## 5. Summary of Key Variables & Types

- **`Context`**: Used across helpers and adapters to access system resources.
- **`ArrayList<T>`**: Standard container for lists of data models (Subjects, Reviews, etc.).
- **`SQLiteDatabase`**: Object used to execute queries against the local storage.
- **`Intents`**: Used for navigation between different Activities.
