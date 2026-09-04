# Remindly: Formal Project Documentation

## 1. Project Overview
**Remindly** is a specialized Android application designed to serve as a comprehensive academic hub for students. Built with a unique **Neo-brutalist "So Matcha" aesthetic**, the app combines bold design with essential productivity tools. It bridges the gap between a standard planner and a focus-enhancing utility, helping students manage their subjects, review materials, and study time in a single, high-contrast, and playful environment.

## 2. Purpose of the Application
The primary purpose of Remindly is to combat student burnout and digital distraction. By consolidating academic schedules, tasks, and materials into one dashboard—and providing a "Study Mission" mode that physically locks distracting applications—the app creates a focused "clean space" for learning. It aims to transform academic management from a chore into a rewarding, gamified experience.

---

## 3. Project Objectives

### **General Objective**
To provide students with an all-in-one productivity platform that streamlines academic organization and maximizes focus through integrated app-locking and gamified task tracking.

### **Specific Objectives**
*   **Subject-Centric Architecture**: To organize review sessions, to-dos, and materials around specific academic subjects for better data retrieval.
*   **Active Distraction Mitigation**: To develop a "Study Mission" feature using Android's Accessibility and WindowManager services to effectively block non-academic apps.
*   **Automated Progress Monitoring**: To implement an intelligent to-do system that automatically identifies missed deadlines and calculates academic consistency.
*   **Gamification and Retention**: To encourage consistent study habits through an XP (Experience Points) and Rank system.
*   **Data Longevity**: To provide a Global Archive system that allows users to manage multiple semesters of data without cluttering their active workspace.

---

## 4. Scope
*   **Personal Academic Hub**: Managing subjects, instructors, and room locations.
*   **Task Management**: "New To-do" system with descriptions, sub-tasks, and deadlines.
*   **Resource Management**: Local storage of PDF/Word files, web links, and digital notes tied to study sessions.
*   **Focus Engineering**: Local app-blocking blocklist and Pomodoro timer.
*   **Local Persistence**: All data is stored in a structured SQLite database for offline access.
*   **Reporting**: Visual summaries of weekly study hours and task completion rates.

## 5. Delimitation
*   **Platform**: Exclusively developed for the Android operating system.
*   **Connectivity**: Does not feature cloud synchronization or multi-device login (Local storage only).
*   **Collaboration**: No peer-to-peer sharing or group study features.
*   **Automation**: Does not automatically sync with external Learning Management Systems (LMS) like Canvas or Moodle.
*   **Scope of Data**: Focuses purely on academic management, excluding personal budget tracking or fitness.

---

## 6. Features & App Runthrough

### **Key Features**
*   **The "Study Mission"**: A focused locking state where the user chooses apps to block. It features a high-contrast overlay that prevents app usage until the timer ends or a PIN is entered.
*   **Smart Status To-dos**: Tasks that cycle through `Pending`, `In Progress`, and `Completed`, with an automated `Missed` state based on the current date.
*   **Dynamic Calendar**: A month-view selector that filters and displays specific class schedules and review sessions for any given day.
*   **Archive/Restore Logic**: A "Vanish-Proof" system to hide old data and restore it from Settings whenever needed.

### **App Runthrough**
1.  **Welcome & Setup**: The user registers and enters their student profile.
2.  **Organization**: The user adds their "Application Development" subject and sets a recurring schedule.
3.  **Preparation**: Within that subject, they create a "Programming Midterm" review session and add "New To-dos."
4.  **Resource Loading**: The user attaches lecture notes and tutorial links to the session.
5.  **Focus Phase**: The user activates a "Study Mission" to block TikTok and Facebook for 2 hours.
6.  **Progress Tracking**: As they work, they tap the status badge on their to-dos to move from "Pending" to "In Progress."
7.  **Completion**: Upon marking tasks as "Completed," the user receives XP and views their rank climb on the dashboard.

---

## 7. Logo Creation

### **Concept**
The logo is designed to reflect the **"Focus through Calm"** philosophy. It uses the Neo-brutalist style—characterized by thick black outlines and offset shadows—to symbolize stability and academic rigor, while the pastel "So Matcha" colors provide a retro, low-stress vibe.

### **Elements**
*   **Icon**: A stylized **Matcha Cup (Chawan)** featuring a digital clock face on its side. A small Matcha Leaf is placed at the center of the clock, acting as both a focus indicator and a "completion checkmark."
*   **Typography**: The name "Remindly" is written in **Comfortaa Bold**, a rounded, friendly font that contrasts against the sharp, blocky edges of the icon.
*   **Theme Integration**:
    *   **Tea Green (#C8D69B)**: The primary fill for the cup, representing the focus-giving matcha.
    *   **Celtic Blue (#3971B8)**: The highlight for the clock hands and the "i" in the text.
    *   **3dp Black Stroke**: A heavy outline around all elements to maintain the Neo-brutalist "comic book" or "retro" feel.

---

## 8. Benefits

### **For Users**
*   **Reduced Friction**: No more jumping between different apps for schedule, notes, and task lists.
*   **Enhanced Self-Discipline**: The app-locker provides a physical barrier to distraction, helping users build better focus habits.
*   **Visual Gratification**: The gamified level system and Neo-brutalist UI make academic management feel like a game rather than a burden.
*   **Data Integrity**: Users can clear their workspace (Archive) without the anxiety of permanently losing their hard-earned notes or records.

### **For Future Development**
*   **Scalability**: The modular subject-review-todo database structure allows for future integration of Pomodoro analytics and study-group networking.
*   **Automation Path**: Delimitations in cloud sync and LMS integration provide a clear roadmap for version 2.0 (Remindly Pro).
*   **Platform Expansion**: The established branding and Neo-brutalist design language are easily portable to web or cross-platform frameworks (Flutter/React Native).
