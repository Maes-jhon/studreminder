# Remindly: Comprehensive Feature Report & Brand Identity

This report provides a detailed runthrough of the **Remindly** application's features and proposes a logo design concept aligned with its unique **"So Matcha" Neo-brutalist** aesthetic.

---

## 1. Core Features

### **A. Academic Organization Hub**
*   **Subject Management**: Create a digital directory of your academic subjects. Each subject includes the instructor's name, room location, and a color-coded identifier.
*   **Detailed Subject Insights**: A central hub for each subject showing all associated review sessions, teachers, and locations.
*   **Intelligent To-do System**:
    *   Multi-field task creation (Description, Sub-tasks, Deadlines).
    *   **Manual Status Toggling**: Cycle through `Pending` -> `In Progress` -> `Completed`.
    *   **Auto-Missed Logic**: Tasks automatically turn red and mark as `Missed` if the deadline passes without completion.
*   **Resource Library**: Attach PDF/Word files, save important lecture links, and write detailed notes directly within specific review sessions.

### **B. Scheduling & Planning**
*   **Dynamic Class Schedule**: A day-by-day view of your classes with start/end times and room numbers.
*   **Integrated Month Calendar**: A functional month-view calendar that filters and displays your schedule for any selected day.
*   **Smart Reminders**: Automated notifications (5 min, 15 min, 30 min, or 1 hour before) to ensure you never miss a class or a review.

### **C. Focus Mode & App Locking ("Study Mission")**
*   **Custom Blocklist**: Select distracting apps (e.g., TikTok, Facebook) to be restricted during focus time.
*   **The "Study Mission" Overlay**: A non-bypassable WindowManager overlay that appears when a blocked app is opened.
*   **Pomodoro Integration**: A built-in focus timer with customized "Study" and "Break" intervals.
*   **Emergency PIN**: A secure 4-digit PIN system in Settings to allow temporary bypass in urgent situations.

### **D. Gamification & Reports**
*   **Academic XP System**: Earn experience points for completing tasks and attending review sessions.
*   **Rank Progression**: Level up from a "Novice Scholar" to "The Grand Scholar."
*   **Weekly Performance Report**: A high-level visual summary of tasks completed and total study hours.

### **E. Advanced Data Management**
*   **Global Archive System**: A dedicated management screen to hide old subjects or completed reviews without losing their data, with one-tap restoration.
*   **Persistence**: Full SQLite database integration ensures all data is saved locally and remains available offline.

---

## 2. App Runthrough (The User Journey)

1.  **Onboarding**: The user is greeted by a playful **Welcome Screen** in Ivory and Matcha Green. They register an account or log in to access their personal academic dashboard.
2.  **Dashboard Hub**: The **Home Screen** shows their current level, "Today's Mission" (next class), and "Upcoming Reviews."
3.  **Organization**: The user navigates to **Subjects** to add their courses. Within a Subject, they create a **Review Session** for an upcoming exam.
4.  **Preparation**: Inside the Review Session, they add a **New To-do** (e.g., "Review Chapter 3"), attach a PDF of lecture slides, and save a YouTube tutorial link.
5.  **Execution (Focus)**: Tapping **"Start Study Mission"** activates the Focus Blocker. If the user tries to open a distracting app, the **Lock Overlay** appears with a timer and a motivational quote.
6.  **Progress**: Once the task is done, the user marks it as **Completed**, earning +10 XP. They check the **Report** screen to see their weekly consistency.
7.  **Cleanup**: After the semester, the user **Archives** their subjects to keep the interface clean, knowing they can restore them from **Settings** anytime.

---

## 3. Logo Creation Concept

### **The Vision**
The logo must bridge the gap between "Focus/Academic Rigor" and "Playful/Retro Calm." It should leverage the **Neo-brutalist** design language: bold, chunky, and high-contrast.

### **Design Elements**
*   **Primary Icon**: A **Matcha Cup (Chawan)** with a digital clock face or an "alarm bell" icon subtly integrated into the liquid surface. Alternatively, a single Matcha Leaf shaped like a "Checkmark."
*   **Style**: Flat design with a **thick 3dp black stroke** and a offset **hard shadow** (down and to the right).
*   **Typography**: The word "Remindly" in **Comfortaa Bold**, with the "i" replaced by a small matcha leaf.

### **Color Palette (So Matcha)**
| Element | Hex Code | Purpose |
| :--- | :--- | :--- |
| **Main Body** | `#C8D69B` (Tea Green) | Represents the calm, focused energy of matcha. |
| **Accents** | `#FFEC89` (Vanilla/Yellow) | Used for the "alarm" or "focus" highlights. |
| **Base/Text** | `#343B1B` (Dark Brown) | Used for the logo text and inner details. |
| **Border** | `#000000` (True Black) | The characteristic Neo-brutalist chunky outline. |

### **Proposed Logo Variants**
1.  **The "Focus Cup"**: A matcha cup with a steaming "25:00" (Pomodoro) emerging from it.
2.  **The "Retro SM"**: A chunky, stylized "SM" (So Matcha) monogram with an offset shadow, where the 'S' looks like a winding clock spring.

---

> [!TIP]
> To maintain the Neo-brutalist brand, the logo should never use gradients or soft shadows. Stick to solid fills and harsh, blocky shapes!
