package com.example.studreminder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class AddSubjectActivity extends AppCompatActivity {

    TextInputEditText etSubject, etTeacher, etRoom;
    Button btnSaveSubject;

    DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private int subjectId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        etSubject = findViewById(R.id.etSubject);
        etTeacher = findViewById(R.id.etTeacher);
        etRoom = findViewById(R.id.etRoom);
        btnSaveSubject = findViewById(R.id.btnSaveSubject);

        databaseHelper = new DatabaseHelper(this);

        // Check for Edit Mode
        if (getIntent().hasExtra("subject_id")) {
            isEditMode = true;
            subjectId = getIntent().getIntExtra("subject_id", -1);
            etSubject.setText(getIntent().getStringExtra("subject_name"));
            etTeacher.setText(getIntent().getStringExtra("subject_teacher"));
            etRoom.setText(getIntent().getStringExtra("subject_room"));
            btnSaveSubject.setText("Update Subject");
        }

        btnSaveSubject.setOnClickListener(v -> {

            String subject = etSubject.getText().toString().trim();
            String teacher = etTeacher.getText().toString().trim();
            String room = etRoom.getText().toString().trim();

            if(subject.isEmpty() || teacher.isEmpty() || room.isEmpty()){
                Toast.makeText(this,"Complete all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (isEditMode) {
                success = databaseHelper.updateSubject(
                        subjectId,
                        subject,
                        teacher,
                        room,
                        0xFFFFD54F
                );
            } else {
                success = databaseHelper.insertSubject(
                        subject,
                        teacher,
                        room,
                        0xFFFFD54F
                );
            }

            if(success){
                Toast.makeText(this, isEditMode ? "Subject Updated" : "Subject Added", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
            }

        });

    }
}
