package com.example.studreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StudentReminder.db";
    public static final int DATABASE_VERSION = 19;

    // ==========================
    // SCHEDULE TABLE
    // ==========================
    public static final String TABLE_SCHEDULE = "schedule";
    public static final String COL_ID = "id";
    public static final String COL_SUBJECT = "subject";
    public static final String COL_DAY = "day";
    public static final String COL_START = "start_time";
    public static final String COL_END = "end_time";
    public static final String COL_ROOM = "room";
    public static final String COL_IS_ARCHIVED = "is_archived";
    public static final String COL_SCHEDULE_REMINDER = "reminder_offset";

    // ==========================
    // SUBJECT TABLE
    // ==========================
    public static final String TABLE_SUBJECT = "subjects";
    public static final String COL_SUBJECT_ID = "subject_id";
    public static final String COL_SUBJECT_NAME = "subject_name";
    public static final String COL_SUBJECT_TEACHER = "subject_teacher";
    public static final String COL_SUBJECT_ROOM = "subject_room";
    public static final String COL_SUBJECT_COLOR = "subject_color";

    // ==========================
    // REVIEW TABLE
    // ==========================
    public static final String TABLE_REVIEW = "reviews";
    public static final String COL_REVIEW_ID = "review_id";
    public static final String COL_REVIEW_SUBJECT = "subject";
    public static final String COL_REVIEW_TOPIC = "topic";
    public static final String COL_REVIEW_DATE = "date";
    public static final String COL_REVIEW_TIME = "time";
    public static final String COL_REVIEW_REMINDER = "reminder";
    public static final String COL_REVIEW_STATUS = "status";

    // ==========================
    // TODO TABLE
    // ==========================
    public static final String TABLE_TODO = "todos";
    public static final String COL_TODO_ID = "todo_id";
    public static final String COL_TODO_REVIEW_ID = "review_id";
    public static final String COL_TODO_TASK = "task";
    public static final String COL_TODO_COMPLETED = "completed";
    public static final String COL_TODO_DESC = "description";
    public static final String COL_TODO_SUB = "sub_todos";
    public static final String COL_TODO_DEADLINE = "deadline";
    public static final String COL_TODO_STATUS = "status";
    public static final String COL_TODO_EMOJI = "emoji";
    public static final String COL_TODO_COLOR = "color";
    public static final String COL_TODO_LABEL = "label";
    public static final String COL_TODO_LABEL_TYPE = "label_type";

    // ==========================
    // REVIEW FILE TABLE
    // ==========================
    public static final String TABLE_REVIEW_FILE = "review_files";
    public static final String COL_FILE_ID = "file_id";
    public static final String COL_FILE_REVIEW_ID = "review_id";
    public static final String COL_FILE_NAME = "file_name";
    public static final String COL_FILE_URI = "file_uri";

    // ==========================
    // REVIEW LINK TABLE
    // ==========================
    public static final String TABLE_REVIEW_LINK = "review_links";
    public static final String COL_LINK_ID = "link_id";
    public static final String COL_LINK_REVIEW_ID = "review_id";
    public static final String COL_LINK_TITLE = "link_title";
    public static final String COL_LINK_URL = "link_url";

    // ==========================
    // STUDY SET TABLE
    // ==========================
    public static final String TABLE_STUDY_SET = "study_sets";
    public static final String COL_STUDY_ID = "study_id";
    public static final String COL_STUDY_REVIEW_ID = "review_id";
    public static final String COL_STUDY_QUESTION = "question";
    public static final String COL_STUDY_ANSWER = "answer";

    // ==========================
    // REVIEW NOTE TABLE
    // ==========================
    public static final String TABLE_REVIEW_NOTE = "review_notes";
    public static final String COL_NOTE_ID = "note_id";
    public static final String COL_NOTE_REVIEW_ID = "review_id";
    public static final String COL_NOTE_TITLE = "note_title";
    public static final String COL_NOTE_CONTENT = "note_content";

    // ==========================
    // USER TABLE
    // ==========================
    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_SCHOOL = "school";
    public static final String COL_BIRTHDAY = "birthday";
    public static final String COL_YEAR_LEVEL = "year_level";
    public static final String COL_COURSE = "course";
    public static final String COL_PROFILE_IMAGE = "profile_image";

    // ==========================
    // DATABASE CONSTRUCTOR
    // ==========================
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SCHEDULE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUBJECT + " TEXT NOT NULL, " +
                COL_DAY + " TEXT NOT NULL, " +
                COL_START + " TEXT NOT NULL, " +
                COL_END + " TEXT NOT NULL, " +
                COL_ROOM + " TEXT NOT NULL, " +
                COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0, " +
                COL_SCHEDULE_REMINDER + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_SUBJECT + " (" +
                COL_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUBJECT_NAME + " TEXT NOT NULL, " +
                COL_SUBJECT_TEACHER + " TEXT NOT NULL, " +
                COL_SUBJECT_ROOM + " TEXT NOT NULL, " +
                COL_SUBJECT_COLOR + " INTEGER NOT NULL, " +
                COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REVIEW + " (" +
                COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REVIEW_SUBJECT + " TEXT NOT NULL, " +
                COL_REVIEW_TOPIC + " TEXT NOT NULL, " +
                COL_REVIEW_DATE + " TEXT NOT NULL, " +
                COL_REVIEW_TIME + " TEXT NOT NULL, " +
                COL_REVIEW_REMINDER + " TEXT NOT NULL, " +
                COL_REVIEW_STATUS + " TEXT, " +
                COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_TODO + " (" +
                COL_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TODO_REVIEW_ID + " INTEGER NOT NULL, " +
                COL_TODO_TASK + " TEXT NOT NULL, " +
                COL_TODO_COMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                COL_TODO_DESC + " TEXT, " +
                COL_TODO_SUB + " TEXT, " +
                COL_TODO_DEADLINE + " TEXT, " +
                COL_TODO_STATUS + " TEXT DEFAULT 'Pending', " +
                COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0, " +
                COL_TODO_EMOJI + " TEXT, " +
                COL_TODO_COLOR + " INTEGER, " +
                COL_TODO_LABEL + " TEXT, " +
                COL_TODO_LABEL_TYPE + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REVIEW_FILE + " (" +
                COL_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FILE_REVIEW_ID + " INTEGER NOT NULL, " +
                COL_FILE_NAME + " TEXT NOT NULL, " +
                COL_FILE_URI + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REVIEW_LINK + " (" +
                COL_LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LINK_REVIEW_ID + " INTEGER NOT NULL, " +
                COL_LINK_TITLE + " TEXT NOT NULL, " +
                COL_LINK_URL + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_STUDY_SET + " (" +
                COL_STUDY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDY_REVIEW_ID + " INTEGER NOT NULL, " +
                COL_STUDY_QUESTION + " TEXT NOT NULL, " +
                COL_STUDY_ANSWER + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REVIEW_NOTE + " (" +
                COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTE_REVIEW_ID + " INTEGER NOT NULL, " +
                COL_NOTE_TITLE + " TEXT NOT NULL, " +
                COL_NOTE_CONTENT + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_FULL_NAME + " TEXT, " +
                COL_SCHOOL + " TEXT, " +
                COL_BIRTHDAY + " TEXT, " +
                COL_YEAR_LEVEL + " TEXT, " +
                COL_COURSE + " TEXT, " +
                COL_PROFILE_IMAGE + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 15) {
            // Schedule table
            db.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0");

            // Subjects table
            db.execSQL("ALTER TABLE " + TABLE_SUBJECT + " ADD COLUMN " + COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0");

            // Reviews table
            db.execSQL("ALTER TABLE " + TABLE_REVIEW + " ADD COLUMN " + COL_REVIEW_STATUS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_REVIEW + " ADD COLUMN " + COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0");

            // Todos table
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_DESC + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_SUB + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_DEADLINE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_STATUS + " TEXT DEFAULT 'Pending'");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_IS_ARCHIVED + " INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 16) {
            db.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + COL_SCHEDULE_REMINDER + " TEXT");
        }
        if (oldVersion < 17) {
            // User table profile fields
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_FULL_NAME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_SCHOOL + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_BIRTHDAY + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_YEAR_LEVEL + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_PROFILE_IMAGE + " TEXT");

            // Todo table enhancements
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_EMOJI + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_COLOR + " INTEGER");
        }
        if (oldVersion < 18) {
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_LABEL + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_TODO_LABEL_TYPE + " TEXT");
        }
        if (oldVersion < 19) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_COURSE + " TEXT");
        }
    }

    // ==========================================================
    // HELPER METHOD FOR SCHEDULES
    // ==========================================================
    private Schedule createScheduleFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
        String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
        String day = cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_START));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_END));
        String room = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM));
        String reminder = cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHEDULE_REMINDER));

        return new Schedule(id, subject, day, startTime, endTime, room, 0, reminder);
    }

    // ==========================================================
    // SUBJECT FUNCTIONS
    // ==========================================================
    public boolean insertSubject(String subject, String teacher, String room, int color) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUBJECT_NAME, subject);
        values.put(COL_SUBJECT_TEACHER, teacher);
        values.put(COL_SUBJECT_ROOM, room);
        values.put(COL_SUBJECT_COLOR, color);
        long result = db.insert(TABLE_SUBJECT, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjectList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBJECT + " WHERE " + COL_IS_ARCHIVED + "=0 ORDER BY " + COL_SUBJECT_NAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SUBJECT_ID));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_NAME));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_TEACHER));
                String room = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_ROOM));
                int color = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SUBJECT_COLOR));

                subjectList.add(new Subject(id, subject, teacher, room, color));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjectList;
    }

    public boolean deleteSubject(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_SUBJECT, COL_SUBJECT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean updateSubject(int id, String subject, String teacher, String room, int color) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUBJECT_NAME, subject);
        values.put(COL_SUBJECT_TEACHER, teacher);
        values.put(COL_SUBJECT_ROOM, room);
        values.put(COL_SUBJECT_COLOR, color);

        int result = db.update(TABLE_SUBJECT, values, COL_SUBJECT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // SCHEDULE FUNCTIONS
    // ==========================================================
    public long insertSchedule(String subject, String day, String startTime, String endTime, String room, String reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUBJECT, subject);
        values.put(COL_DAY, day);
        values.put(COL_START, startTime);
        values.put(COL_END, endTime);
        values.put(COL_ROOM, room);
        values.put(COL_SCHEDULE_REMINDER, reminder);

        long result = db.insert(TABLE_SCHEDULE, null, values);
        db.close();
        return result;
    }

    public ArrayList<Schedule> getAllSchedules() {
        ArrayList<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + COL_IS_ARCHIVED + "=0", null);

        if (cursor.moveToFirst()) {
            do {
                scheduleList.add(createScheduleFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    public ArrayList<Schedule> getSchedulesByDay(String day) {
        ArrayList<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + COL_DAY + " LIKE ? AND " + COL_IS_ARCHIVED + "=0", new String[]{"%" + day + "%"});

        if (cursor.moveToFirst()) {
            do {
                scheduleList.add(createScheduleFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    public boolean deleteSchedule(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_SCHEDULE, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean updateSchedule(int id, String subject, String day, String startTime, String endTime, String room, String reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUBJECT, subject);
        values.put(COL_DAY, day);
        values.put(COL_START, startTime);
        values.put(COL_END, endTime);
        values.put(COL_ROOM, room);
        values.put(COL_SCHEDULE_REMINDER, reminder);

        int result = db.update(TABLE_SCHEDULE, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // REVIEW FUNCTIONS
    // ==========================================================
    public long insertReview(String subject, String topic, String date, String time, String reminder, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REVIEW_SUBJECT, subject);
        values.put(COL_REVIEW_TOPIC, topic);
        values.put(COL_REVIEW_DATE, date);
        values.put(COL_REVIEW_TIME, time);
        values.put(COL_REVIEW_REMINDER, reminder);
        values.put(COL_REVIEW_STATUS, status);

        long result = db.insert(TABLE_REVIEW, null, values);
        db.close();
        return result;
    }

    public ArrayList<Review> getAllReviews() {
        ArrayList<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW + " WHERE " + COL_IS_ARCHIVED + "=0", null);

        if (cursor.moveToFirst()) {
            do {
                reviewList.add(new Review(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TOPIC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_REMINDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ARCHIVED)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reviewList;
    }

    public ArrayList<Review> getReviewsBySubject(String subject) {
        ArrayList<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW + " WHERE " + COL_REVIEW_SUBJECT + "=? AND " + COL_IS_ARCHIVED + "=0", new String[]{subject});

        if (cursor.moveToFirst()) {
            do {
                reviewList.add(new Review(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TOPIC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_REMINDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ARCHIVED)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reviewList;
    }

    public boolean deleteReview(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_REVIEW, COL_REVIEW_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean updateReview(int id, String subject, String topic, String date, String time, String reminder, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REVIEW_SUBJECT, subject);
        values.put(COL_REVIEW_TOPIC, topic);
        values.put(COL_REVIEW_DATE, date);
        values.put(COL_REVIEW_TIME, time);
        values.put(COL_REVIEW_REMINDER, reminder);
        values.put(COL_REVIEW_STATUS, status);

        int result = db.update(TABLE_REVIEW, values, COL_REVIEW_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // TODO FUNCTIONS
    // ==========================================================
    public boolean insertTodo(int reviewId, String task, String description, String subTodos, String deadline, String label, String labelType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_REVIEW_ID, reviewId);
        values.put(COL_TODO_TASK, task);
        values.put(COL_TODO_COMPLETED, 0);
        values.put(COL_TODO_DESC, description);
        values.put(COL_TODO_SUB, subTodos);
        values.put(COL_TODO_DEADLINE, deadline);
        values.put(COL_TODO_STATUS, "Ongoing");
        values.put(COL_TODO_LABEL, label);
        values.put(COL_TODO_LABEL_TYPE, labelType);

        long result = db.insert(TABLE_TODO, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Todo> getTodosByReviewId(int reviewId) {
        ArrayList<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + COL_TODO_REVIEW_ID + "=? AND " + COL_IS_ARCHIVED + "=0", new String[]{String.valueOf(reviewId)});

        if (cursor.moveToFirst()) {
            do {
                todoList.add(new Todo(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_REVIEW_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_TASK)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_SUB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_DEADLINE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ARCHIVED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_LABEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_LABEL_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_COLOR))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return todoList;
    }

    public boolean updateTodoStatus(int id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_STATUS, status);
        if (status.equals("Completed")) {
            values.put(COL_TODO_COMPLETED, 1);
        } else {
            values.put(COL_TODO_COMPLETED, 0);
        }

        int result = db.update(TABLE_TODO, values, COL_TODO_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean archiveItem(String table, String idColumn, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_ARCHIVED, 1);
        int result = db.update(table, values, idColumn + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean unarchiveItem(String table, String idColumn, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_ARCHIVED, 0);
        int result = db.update(table, values, idColumn + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean permanentlyDeleteItem(String table, String idColumn, int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(table, idColumn + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public ArrayList<Subject> getArchivedSubjects() {
        ArrayList<Subject> subjectList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBJECT + " WHERE " + COL_IS_ARCHIVED + "=1 ORDER BY " + COL_SUBJECT_NAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SUBJECT_ID));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_NAME));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_TEACHER));
                String room = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_ROOM));
                int color = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SUBJECT_COLOR));

                subjectList.add(new Subject(id, subject, teacher, room, color));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjectList;
    }

    public ArrayList<Schedule> getArchivedSchedules() {
        ArrayList<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + COL_IS_ARCHIVED + "=1", null);

        if (cursor.moveToFirst()) {
            do {
                scheduleList.add(createScheduleFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    public ArrayList<Review> getArchivedReviews() {
        ArrayList<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW + " WHERE " + COL_IS_ARCHIVED + "=1", null);

        if (cursor.moveToFirst()) {
            do {
                reviewList.add(new Review(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TOPIC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_REMINDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ARCHIVED)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reviewList;
    }

    public ArrayList<Todo> getArchivedTodos() {
        ArrayList<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + COL_IS_ARCHIVED + "=1", null);

        if (cursor.moveToFirst()) {
            do {
                todoList.add(new Todo(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_REVIEW_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_TASK)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_SUB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_DEADLINE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ARCHIVED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_LABEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TODO_LABEL_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TODO_COLOR))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return todoList;
    }

    public boolean updateTodoCompleted(int id, boolean completed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_COMPLETED, completed ? 1 : 0);

        int result = db.update(TABLE_TODO, values, COL_TODO_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteTodo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_TODO, COL_TODO_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // REVIEW FILE FUNCTIONS
    // ==========================================================
    public boolean insertReviewFile(int reviewId, String fileName, String fileUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FILE_REVIEW_ID, reviewId);
        values.put(COL_FILE_NAME, fileName);
        values.put(COL_FILE_URI, fileUri);

        long result = db.insert(TABLE_REVIEW_FILE, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<ReviewFile> getFilesByReviewId(int reviewId) {
        ArrayList<ReviewFile> fileList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW_FILE + " WHERE " + COL_FILE_REVIEW_ID + "=?", new String[]{String.valueOf(reviewId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILE_ID));
                int savedReviewId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILE_REVIEW_ID));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_NAME));
                String fileUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_URI));

                fileList.add(new ReviewFile(id, savedReviewId, fileName, fileUri));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fileList;
    }

    public boolean deleteReviewFile(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_REVIEW_FILE, COL_FILE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // REVIEW LINK FUNCTIONS
    // ==========================================================
    public boolean insertReviewLink(int reviewId, String title, String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LINK_REVIEW_ID, reviewId);
        values.put(COL_LINK_TITLE, title);
        values.put(COL_LINK_URL, url);

        long result = db.insert(TABLE_REVIEW_LINK, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<ReviewLink> getLinksByReviewId(int reviewId) {
        ArrayList<ReviewLink> linkList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW_LINK + " WHERE " + COL_LINK_REVIEW_ID + "=?", new String[]{String.valueOf(reviewId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LINK_ID));
                int savedReviewId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LINK_REVIEW_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_LINK_TITLE));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(COL_LINK_URL));

                linkList.add(new ReviewLink(id, savedReviewId, title, url));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return linkList;
    }

    public boolean deleteReviewLink(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_REVIEW_LINK, COL_LINK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // STUDY SET FUNCTIONS
    // ==========================================================
    public boolean insertStudySet(int reviewId, String question, String answer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDY_REVIEW_ID, reviewId);
        values.put(COL_STUDY_QUESTION, question);
        values.put(COL_STUDY_ANSWER, answer);

        long result = db.insert(TABLE_STUDY_SET, null, values);
        db.close();
        return result != -1;
    }

    // ==========================================================
    // REVIEW NOTE FUNCTIONS
    // ==========================================================
    public boolean insertReviewNote(int reviewId, String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_REVIEW_ID, reviewId);
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_CONTENT, content);

        long result = db.insert(TABLE_REVIEW_NOTE, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<ReviewNote> getNotesByReviewId(int reviewId) {
        ArrayList<ReviewNote> noteList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REVIEW_NOTE + " WHERE " + COL_NOTE_REVIEW_ID + "=?", new String[]{String.valueOf(reviewId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTE_ID));
                int revId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTE_REVIEW_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_CONTENT));

                noteList.add(new ReviewNote(id, revId, title, content));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public boolean deleteReviewNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_REVIEW_NOTE, COL_NOTE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==========================================================
    // USER / AUTH FUNCTIONS
    // ==========================================================
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // ==========================
    // USER PROFILE FUNCTIONS
    // ==========================
    public boolean updateProfile(int userId, String fullName, String school, String course, String birthday, String yearLevel, String profileImage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_SCHOOL, school);
        values.put(COL_COURSE, course);
        values.put(COL_BIRTHDAY, birthday);
        values.put(COL_YEAR_LEVEL, yearLevel);
        values.put(COL_PROFILE_IMAGE, profileImage);

        int result = db.update(TABLE_USER, values, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    public Cursor getUserProfile(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + "=?", new String[]{username});
    }

    // ==========================================================
    // REPORT FUNCTIONS
    // ==========================================================
    public ArrayList<PerformanceData> getWeeklyPerformance() {
        ArrayList<PerformanceData> performanceList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SCHEDULE + " WHERE " + COL_DAY + " LIKE ?", new String[]{"%" + day + "%"});
            int scheduleCount = 0;
            if (cursor.moveToFirst()) {
                scheduleCount = cursor.getInt(0);
            }
            cursor.close();

            int durationMinutes = scheduleCount * 60;
            int tasksCompleted = scheduleCount;
            int totalTasks = scheduleCount > 0 ? scheduleCount : 0;

            // Matches constructor: PerformanceData(String day, int durationMinutes, int tasksCompleted, int totalTasks)
            performanceList.add(new PerformanceData(day, durationMinutes, tasksCompleted, totalTasks));
        }

        db.close();
        return performanceList;
    }
}