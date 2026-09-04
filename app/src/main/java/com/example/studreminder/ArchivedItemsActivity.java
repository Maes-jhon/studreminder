package com.example.studreminder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ArchivedItemsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvArchivedItems;
    private TextView tvEmptyArchive;
    private ImageButton btnBack;
    private DatabaseHelper db;
    private ArchivedItemsAdapter adapter;
    private List<ArchivedItemModel> archivedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_items);

        db = new DatabaseHelper(this);

        tabLayout = findViewById(R.id.tabLayout);
        rvArchivedItems = findViewById(R.id.rvArchivedItems);
        tvEmptyArchive = findViewById(R.id.tvEmptyArchive);
        btnBack = findViewById(R.id.btnBack);

        rvArchivedItems.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());

        setupTabs();
        loadArchivedItems(0); // Load Subjects by default
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Subjects"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.addTab(tabLayout.newTab().setText("Schedule"));
        tabLayout.addTab(tabLayout.newTab().setText("To-dos"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadArchivedItems(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadArchivedItems(int position) {
        archivedList.clear();

        if (position == 0) {
            ArrayList<Subject> subjects = db.getArchivedSubjects();
            for (Subject s : subjects) {
                archivedList.add(new ArchivedItemModel(s.getId(), s.getSubject(), s.getTeacher(), DatabaseHelper.TABLE_SUBJECT, DatabaseHelper.COL_SUBJECT_ID));
            }
        } else if (position == 1) {
            ArrayList<Review> reviews = db.getArchivedReviews();
            for (Review r : reviews) {
                archivedList.add(new ArchivedItemModel(r.getId(), r.getTopic(), r.getDate() + " • " + r.getTime(), DatabaseHelper.TABLE_REVIEW, DatabaseHelper.COL_REVIEW_ID));
            }
        } else if (position == 2) {
            ArrayList<Schedule> schedules = db.getArchivedSchedules();
            for (Schedule s : schedules) {
                archivedList.add(new ArchivedItemModel(s.getId(), s.getSubject(), s.getDate() + " (" + s.getStartTime() + ")", DatabaseHelper.TABLE_SCHEDULE, DatabaseHelper.COL_ID));
            }
        } else if (position == 3) {
            ArrayList<Todo> todos = db.getArchivedTodos();
            for (Todo t : todos) {
                archivedList.add(new ArchivedItemModel(t.getId(), t.getTask(), t.getStatus() + " | Due: " + t.getDeadline(), DatabaseHelper.TABLE_TODO, DatabaseHelper.COL_TODO_ID));
            }
        }

        if (archivedList.isEmpty()) {
            rvArchivedItems.setVisibility(View.GONE);
            tvEmptyArchive.setVisibility(View.VISIBLE);
        } else {
            rvArchivedItems.setVisibility(View.VISIBLE);
            tvEmptyArchive.setVisibility(View.GONE);
            adapter = new ArchivedItemsAdapter(archivedList);
            rvArchivedItems.setAdapter(adapter);
        }
    }
}