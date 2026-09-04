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

    public void addXP(int amount) {
        int currentXP = getXP();
        prefs.edit().putInt(KEY_XP, currentXP + amount).apply();
    }

    public int getXP() {
        return prefs.getInt(KEY_XP, 0);
    }

    public int getLevel() {
        return (getXP() / 100) + 1;
    }

    public int getProgressToNextLevel() {
        return getXP() % 100;
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
}
