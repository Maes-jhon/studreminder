package com.example.studreminder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTotalTasks, tvTotalTime, tvNextRankLabel, tvXPToNextRank;
    private LinearProgressIndicator progressRankXP;
    private RecyclerView recyclerDaily;
    private DatabaseHelper databaseHelper;
    private XPManager xpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        btnBack = findViewById(R.id.btnBack);
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvNextRankLabel = findViewById(R.id.tvNextRankLabel);
        tvXPToNextRank = findViewById(R.id.tvXPToNextRank);
        progressRankXP = findViewById(R.id.progressRankXP);
        recyclerDaily = findViewById(R.id.recyclerDaily);

        databaseHelper = new DatabaseHelper(this);
        xpManager = new XPManager(this);
        
        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadPerformanceData();
        loadXPStats();
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

        // Set up daily missions adapter
        recyclerDaily.setAdapter(new PerformanceAdapter(data));
    }

    private void loadXPStats() {
        int currentLevel = xpManager.getLevel();
        String nextRank = "";
        
        if (currentLevel < 5) nextRank = "ACADEMIC APPRENTICE";
        else if (currentLevel < 10) nextRank = "KNOWLEDGE SEEKER";
        else if (currentLevel < 20) nextRank = "EXAM DESTROYER";
        else if (currentLevel < 35) nextRank = "ACADEMIC WARRIOR";
        else if (currentLevel < 50) nextRank = "THE GRAND SCHOLAR";
        else nextRank = "MAX RANK REACHED";

        tvNextRankLabel.setText("ROAD TO " + nextRank);
        
        int progress = xpManager.getProgressToNextLevel();
        progressRankXP.setProgress(progress);
        tvXPToNextRank.setText((100 - progress) + " XP to go");
    }
}
