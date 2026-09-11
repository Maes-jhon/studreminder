package com.example.studreminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ShareNoteActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent;
    private MaterialAutoCompleteTextView dropdownSubjects, dropdownReviews;
    private MaterialButton btnSave;
    private DatabaseHelper db;

    private List<Subject> subjectList;
    private List<Review> reviewList;
    private int selectedReviewId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_note);

        db = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etShareTitle);
        etContent = findViewById(R.id.etShareContent);
        dropdownSubjects = findViewById(R.id.dropdownSubjects);
        dropdownReviews = findViewById(R.id.dropdownReviews);
        btnSave = findViewById(R.id.btnSaveSharedNote);

        handleIncomingIntent();
        setupDropdowns();

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void handleIncomingIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    etContent.setText(sharedText);
                }
            }
        }
    }

    private void setupDropdowns() {
        subjectList = db.getAllSubjects();
        List<String> subjectNames = new ArrayList<>();
        for (Subject s : subjectList) {
            subjectNames.add(s.getSubject());
        }

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectNames);
        dropdownSubjects.setAdapter(subjectAdapter);

        dropdownSubjects.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSubject = subjectList.get(position).getSubject();
            loadReviewsForSubject(selectedSubject);
        });
    }

    private void loadReviewsForSubject(String subjectName) {
        reviewList = db.getReviewsBySubject(subjectName);
        List<String> reviewTopics = new ArrayList<>();
        for (Review r : reviewList) {
            reviewTopics.add(r.getTopic());
        }

        ArrayAdapter<String> reviewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, reviewTopics);
        dropdownReviews.setAdapter(reviewAdapter);
        dropdownReviews.setText("", false);
        selectedReviewId = -1;

        dropdownReviews.setOnItemClickListener((parent, view, position, id) -> {
            selectedReviewId = reviewList.get(position).getId();
        });
    }

    private void saveNote() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter title and content.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedReviewId == -1) {
            Toast.makeText(this, "Please select a Subject and Review session.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = db.insertReviewNote(selectedReviewId, title, content);
        if (success) {
            Toast.makeText(this, "Note saved to Remindly!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save note.", Toast.LENGTH_SHORT).show();
        }
    }
}