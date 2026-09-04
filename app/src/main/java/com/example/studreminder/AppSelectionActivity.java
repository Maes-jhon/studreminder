package com.example.studreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class AppSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerApps;
    private ProgressBar progressBar;
    private EditText etSearchApp;
    private ImageButton btnBack;
    private AppAdapter adapter;
    private List<AppModel> fullAppList = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        recyclerApps = findViewById(R.id.recyclerApps);
        progressBar = findViewById(R.id.progressBar);
        etSearchApp = findViewById(R.id.etSearchApp);
        btnBack = findViewById(R.id.btnBack);

        prefs = getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);
        recyclerApps.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        etSearchApp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadInstalledApps();
    }

    private void loadInstalledApps() {
        progressBar.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Set<String> savedLocked = prefs.getStringSet("LOCKED_APPS_SET", new HashSet<>());

            fullAppList.clear();
            for (ApplicationInfo app : apps) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = pm.getApplicationLabel(app).toString();
                    Drawable icon = pm.getApplicationIcon(app);
                    String packageName = app.packageName;
                    boolean isLocked = savedLocked.contains(packageName);

                    fullAppList.add(new AppModel(appName, packageName, icon, isLocked));
                }
            }

            fullAppList.sort((a, b) -> a.getAppName().compareToIgnoreCase(b.getAppName()));

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter = new AppAdapter(this, new ArrayList<>(fullAppList));
                recyclerApps.setAdapter(adapter);
            });
        });
    }

    private void filterApps(String query) {
        if (adapter == null) return;
        List<AppModel> filteredList = new ArrayList<>();
        for (AppModel app : fullAppList) {
            if (app.getAppName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(app);
            }
        }
        adapter.updateList(filteredList);
    }
}