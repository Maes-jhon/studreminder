package com.example.studreminder;

import android.graphics.drawable.Drawable;

public class AppModel {
    private String appName;
    private String packageName;
    private Drawable icon;
    private boolean isLocked;

    public AppModel(String appName, String packageName, Drawable icon, boolean isLocked) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.isLocked = isLocked;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
