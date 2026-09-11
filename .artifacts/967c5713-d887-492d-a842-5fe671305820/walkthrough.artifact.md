# Walkthrough - Expanded To-do View

I have implemented a "Click to View" feature for To-do items, ensuring that all task details—including long descriptions and sub-tasks—are easily accessible in a professional, structured dialog.

## Changes Made

### 1. Interactive To-do Cards (TodoAdapter.java)
- **Click Support**: Added a click listener interface to the adapter. Now, tapping anywhere on a To-do card (not just the checkbox or status badge) will trigger the expanded view.
- **Visual Feedback**: The card remains responsive and maintains its Neo-brutalist style during interaction.

### 2. Structured Expansion Dialog (ReviewDetailsActivity.java)
- **Comprehensive View**: Tapping a task opens a custom `AlertDialog` that displays:
    - **Header**: Large, bold task name.
    - **Status Indicator**: A visually matching badge (Ongoing/Completed/Missed) using the same logic as the main list.
    - **Scrollable Details**: Dedicated sections for **Subto-dos** and **Description**, ensuring readability even for very long notes.
    - **Footer**: The deadline is clearly displayed at the bottom with reduced opacity for a clean hierarchy.
- **Consistent Branding**: The dialog uses the **So Matcha** theme, including the Ivory background and Dark Brown typography.

## Verification Results

### Build Status
- **Success**: The project builds successfully with the new expansion logic.

### Functional Check
- **Note Expansion**: Tapping a Note card opens the Note dialog.
- **To-do Expansion**: Tapping a To-do card opens the new To-do detail dialog.
- **Data Integrity**: All fields (Title, Sub-tasks, Description, Deadline) are correctly mapped from the database to the expanded view.

> [!TIP]
> This feature is especially useful for complex tasks like "Project Documentation" where you might have a long list of sub-tasks and detailed instructions saved in the description!
