# Stat Card Personalization & Theme System

This plan enables you to "customize the ID on your own" by adding a **Theme Engine** for the Student Stat Card. You'll be able to choose custom colors and unlock new visual styles as you level up.

## User Review Required

> [!IMPORTANT]
> **Dynamic Themes:** I am adding a "Card Theme" setting. Initially, you can pick from 5 colors (Blue, Pink, Yellow, Green, Purple). More styles will unlock as you reach higher Ranks.

> [!TIP]
> **Rank Icons:** I will add an "Avatar Icon" selector where you can choose a unique symbol (like a sword, book, or star) to represent your current academic path.

## Proposed Changes

### 1. Database & Infrastructure
*   **[MODIFY] [DatabaseHelper.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/DatabaseHelper.java)**:
    *   Upgrade to **Version 20**.
    *   Add `COL_CARD_COLOR` and `COL_RANK_ICON` to the `users` table.
    *   Update `updateProfile` to handle these new personalization fields.

### 2. Customization UI (Edit Profile)
*   **[MODIFY] [activity_edit_profile.xml](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/res/layout/activity_edit_profile.xml)**:
    *   Add a **"Card Theme"** section with clickable color circles.
    *   Add a **"Rank Icon"** horizontal selector (Scrollable list of icons).
*   **[MODIFY] [EditProfileActivity.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/EditProfileActivity.java)**:
    *   Implement logic to select and highlight the chosen color and icon.
    *   Save the selections to the user profile.

### 3. Home Screen Dynamic Styling
*   **[MODIFY] [MainActivity.java](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/java/com/example/studreminder/MainActivity.java)**:
    *   Update `loadUserProfile()` to apply the chosen **Card Color** to the `cardStat` background tint.
    *   Dynamically change the **Progress Bar** and **Level Badge** colors to match the selected theme.
    *   Display the selected **Rank Icon** next to the Name.

### 4. Style Assets
*   **[MODIFY] [bg_id_card.xml](file:///C:/Users/andri/AndroidStudioProjects/studreminder/app/src/main/res/drawable/bg_id_card.xml)**:
    *   Prepare the drawable to support background tinting while keeping the hard shadow.

## Verification Plan

### Manual Verification
1.  **Theme Switch Test**: Go to Edit Profile, pick "Pink" theme, and save. Verify the Home screen card turns Pink and the progress bar matches.
2.  **Icon Selection**: Pick a "Warrior" icon and verify it appears next to your name on the Stat Card.
3.  **Persistence Check**: Restart the app and ensure your custom theme and icon are still active.
