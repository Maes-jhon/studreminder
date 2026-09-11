package com.example.studreminder;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etSchool, etCourse, etYearLevel, etBirthday;
    private MaterialButton btnSave;
    private ImageButton btnBack;
    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        databaseHelper = new DatabaseHelper(this);
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        currentUsername = session.getString("username", "");

        etFullName = findViewById(R.id.etFullName);
        etSchool = findViewById(R.id.etSchool);
        etCourse = findViewById(R.id.etCourse);
        etYearLevel = findViewById(R.id.etYearLevel);
        etBirthday = findViewById(R.id.etBirthday);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);

        loadProfileData();

        btnBack.setOnClickListener(v -> finish());

        etBirthday.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfileData() {
        Cursor cursor = databaseHelper.getUserProfile(currentUsername);
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
            etFullName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME)));
            etSchool.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SCHOOL)));
            etCourse.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURSE)));
            etYearLevel.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_YEAR_LEVEL)));
            etBirthday.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDAY)));
            cursor.close();
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birthday")
                .setTheme(R.style.BrandDatePicker)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            etBirthday.setText(format.format(calendar.getTime()));
        });

        datePicker.show(getSupportFragmentManager(), "BIRTHDAY_PICKER");
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String course = etCourse.getText().toString().trim();
        String year = etYearLevel.getText().toString().trim();
        String bday = etBirthday.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = databaseHelper.updateProfile(userId, name, school, course, bday, year, null);
        if (updated) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }
}
