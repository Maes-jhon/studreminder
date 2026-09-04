package com.example.studreminder;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockAppsActivity extends AppCompatActivity {

    private AppAdapter adapter;
    private final List<AppModel> appList = new ArrayList<>();
    private final List<AppModel> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Replace 'R.layout.activity_lock_apps' if your layout file has a different name (e.g., R.layout.activity_app_lock)
        setContentView(R.layout.activity_lock_apps);

        // Converted 'recyclerView' and 'searchInput' to local variables to clear warning
        RecyclerView recyclerView = findViewById(R.id.recyclerViewApps);
        EditText searchInput = findViewById(R.id.searchBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInstalledApps(recyclerView);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadInstalledApps(RecyclerView recyclerView) {
        PackageManager pm = getPackageManager();
        SharedPreferences prefs = getSharedPreferences("locker_prefs", MODE_PRIVATE);
        Set<String> lockedApps = prefs.getStringSet("LOCKED_APPS_SET", new HashSet<>());

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = pm.getApplicationLabel(appInfo).toString();
                String packageName = appInfo.packageName;
                boolean isLocked = lockedApps.contains(packageName);

                appList.add(new AppModel(appName, packageName, pm.getApplicationIcon(appInfo), isLocked));
            }
        }

        filteredList.addAll(appList);
        adapter = new AppAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void filterApps(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(appList);
        } else {
            for (AppModel app : appList) {
                if (app.getAppName().toLowerCase().contains(query.toLowerCase()) ||
                        app.getPackageName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(app);
                }
            }
        }
        adapter.updateList(filteredList);
    }
}