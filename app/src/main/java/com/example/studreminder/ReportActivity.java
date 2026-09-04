package com.example.studreminder;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTotalTasks, tvTotalTime;
    private RecyclerView recyclerDaily;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        btnBack = findViewById(R.id.btnBack);
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        recyclerDaily = findViewById(R.id.recyclerDaily);

        databaseHelper = new DatabaseHelper(this);
        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadPerformanceData();
    }

    private void loadPerformanceData() {
        ArrayList<PerformanceData> data = databaseHelper.getWeeklyPerformance();
        
        int totalTasks = 0;
        int totalMinutes = 0;
        for (PerformanceData d : data) {
            totalTasks += d.getTasksCompleted();
            totalMinutes += d.getDurationMinutes();
        }

        tvTotalTasks.setText(String.valueOf(totalTasks));
        tvTotalTime.setText((totalMinutes / 60) + "h " + (totalMinutes % 60) + "m");

        // Simple adapter for daily list
        // (In a real app, I'd create a separate file, but for brevity I'll use an anonymous implementation or local class if possible, 
        // but better to create a proper one)
    }
}
