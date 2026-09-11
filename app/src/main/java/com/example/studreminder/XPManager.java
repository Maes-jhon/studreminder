package com.example.studreminder;

import android.content.Context;
import android.content.SharedPreferences;

public class XPManager {
    private static final String PREF_NAME = "xp_prefs";
    private static final String KEY_XP = "current_xp";
    private final SharedPreferences prefs;

    public XPManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Adds XP and returns true if the user reached a new RANK (not just level).
     */
    public boolean addXP(int amount) {
        String oldRank = getRank();
        int currentXP = getXP();
        prefs.edit().putInt(KEY_XP, currentXP + amount).apply();
        String newRank = getRank();
        return !oldRank.equals(newRank);
    }

    public int getXP() {
        return prefs.getInt(KEY_XP, 0);
    }

    public int getLevel() {
        return (getXP() / 100) + 1;
    }

    public int getXPInCurrentLevel() {
        return getXP() % 100;
    }

    public int getRemainingXPForNextLevel() {
        return 100 - getXPInCurrentLevel();
    }

    public int getProgressToNextLevel() {
        return getXPInCurrentLevel();
    }

    public String getRank() {
        int level = getLevel();
        if (level < 5) return "Novice Scholar";
        if (level < 10) return "Academic Apprentice";
        if (level < 20) return "Knowledge Seeker";
        if (level < 35) return "Exam Destroyer";
        if (level < 50) return "Academic Warrior";
        return "The Grand Scholar";
    }

    public String getRankReward(String rank) {
        switch (rank) {
            case "Academic Apprentice": return "Bronze Stat Card Unlocked";
            case "Knowledge Seeker": return "Silver Stat Card Unlocked";
            case "Exam Destroyer": return "Gold Stat Card Unlocked";
            case "Academic Warrior": return "Warrior Badge Unlocked";
            case "The Grand Scholar": return "Master Theme Unlocked";
            default: return "New Title Gained";
        }
    }
}
