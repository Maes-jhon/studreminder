# PROJECT COMPONENTS REPORT: STUDREMINDER

This document identifies and explains the purpose of all classes, variables, constants, and methods used in the **StudReminder** application for group activity submission.

---

## 1. Database Management (Storage Engine)

### [DatabaseHelper.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/DatabaseHelper.java)
**Purpose:** Manages the SQLite database structure and handles all CRUD (Create, Read, Update, Delete) operations for the app's persistent data.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Constant** | `DATABASE_NAME` | `String` | The physical filename "StudentReminder.db". |
| **Constant** | `TABLE_SCHEDULE`, `TABLE_SUBJECT`, `TABLE_REVIEW`, `TABLE_TODO` | `String` | Unique names for the four data tables. |
| **Constant** | `COL_ID`, `COL_SUBJECT`, `COL_ROOM`, etc. | `String` | Column headers for organizing data rows. |
| **Method** | `onCreate()` | `void` | Executes SQL to build the database tables on first launch. |
| **Method** | `insertSubject()` / `insertReview()` | `boolean` | Saves new subject or review records to the database. |
| **Method** | `getAllSubjects()` / `getSchedulesByDay()` | `ArrayList` | Fetches filtered data lists to display in the UI. |
| **Method** | `updateTodoCompleted()` | `boolean` | Specifically toggles the status of a checklist task. |
| **Method** | `deleteReview()` / `deleteSchedule()` | `boolean` | Removes specific records based on their ID. |

---

## 2. User Interface Controllers (Activities)

### [AddReviewActivity.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/AddReviewActivity.java)
**Purpose:** Handles the creation and modification of study/review sessions.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `etSubject`, `etTopic`, `etDate`, `etTime` | `TextInputEditText` | Captures user-entered study details. |
| **Variable** | `dropdownReminder` | `MaterialAutoCompleteTextView` | Allows selection of reminder intervals. |
| **Variable** | `editMode` | `boolean` | Tracks if the user is editing an existing review. |
| **Method** | `saveOrUpdateReview()` | `void` | Processes the form and saves it to the database. |
| **Method** | `showDatePicker()` / `showTimePicker()` | `void` | Displays system dialogs for date and time entry. |

### [ReviewDetailsActivity.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/ReviewDetailsActivity.java)
**Purpose:** Displays the detailed view of a review and its task checklist.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `recyclerTodos` | `RecyclerView` | Container for the list of tasks. |
| **Variable** | `todoAdapter` | `TodoAdapter` | Manages the display of individual task items. |
| **Method** | `showAddTodoDialog()` | `void` | Opens a popup to enter a new checklist task. |
| **Method** | `loadTodos()` | `void` | Fetches the tasks for the specific review session. |

---

## 3. Data Blueprints (Models)

### [Subject.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Subject.java) / [Review.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Review.java) / [Todo.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/Todo.java)
**Purpose:** POJO classes that define the properties of each data entity.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `id` | `int` | The unique database identifier for the object. |
| **Variable** | `subject`, `topic`, `task` | `String` | The primary text data for the object. |
| **Variable** | `completed` | `boolean` | (Todo only) Tracks if a task is checked off. |
| **Method** | `getId()`, `getSubject()`, etc. | `Getter` | Accessor methods for data retrieval. |

---

## 4. UI Logic Bridges (Adapters)

### [SubjectAdapter.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/SubjectAdapter.java) / [TodoAdapter.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/TodoAdapter.java)
**Purpose:** Connects the database objects to the visual components on the screen.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Method** | `onCreateViewHolder()` | `ViewHolder` | Inflates the custom XML layout for each list item. |
| **Method** | `onBindViewHolder()` | `void` | Sets text and icons for the UI based on the data object. |
| **Method** | `updateTaskStyle()` | `void` | (Todo) Adds a strikethrough effect to finished tasks. |

---

## 5. Main Navigation

### [MainActivity.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/MainActivity.java) / [ScheduleActivity.java](file:///C:/Users/ADMIN/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/ScheduleActivity.java)
**Purpose:** Handles the overall flow and navigation of the application.

| Component | Name | Type | Purpose |
| :--- | :--- | :--- | :--- |
| **Variable** | `navHome`, `navSubjects`, `navSchedule` | `LinearLayout` | Buttons for the bottom navigation bar. |
| **Method** | `selectDay()` | `void` | (Schedule) Filters classes by the day of the week. |
| **Method** | `setTodayDate()` | `void` | Automatically updates the UI header to the current date. |

---
**END OF REPORT**
