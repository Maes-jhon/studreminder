package com.example.studreminder;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ReviewDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack, btnArchiveReview;

    private TextView tvSubject;
    private TextView tvInstructor;
    private TextView tvSchedule;
    private TextView tvRoom;
    private TextView tvEmptyDescription;

    private LinearLayout emptyLayout;

    private RecyclerView recyclerTodos;
    private RecyclerView recyclerNotes;
    private RecyclerView recyclerFiles;
    private RecyclerView recyclerLinks;

    private TabLayout tabLayout;
    private MaterialButton btnAdd, btnImportWord;

    private DatabaseHelper databaseHelper;

    // TODO
    private TodoAdapter todoAdapter;
    private ArrayList<Todo> todoList;

    // NOTES
    private ReviewNoteAdapter reviewNoteAdapter;
    private ArrayList<ReviewNote> reviewNoteList;

    // FILES
    private ReviewFileAdapter reviewFileAdapter;
    private ArrayList<ReviewFile> reviewFileList;

    // LINKS
    private ReviewLinkAdapter reviewLinkAdapter;
    private ArrayList<ReviewLink> reviewLinkList;

    private int reviewId = -1;
    private int selectedTabPosition = 0;


    // ==========================================================
    // FILE PICKERS
    // ==========================================================

    private final ActivityResultLauncher<String[]> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            saveSelectedFile(uri);
                        }
                    }
            );

    private final ActivityResultLauncher<String[]> wordPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            importFromWord(uri);
                        }
                    }
            );


    // ==========================================================
    // ON CREATE
    // ==========================================================

    private String currentFilter = "Ongoing";
    private TextView tvTodoFilter;
    private View layoutFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);

        btnBack = findViewById(R.id.btnBack);
        btnArchiveReview = findViewById(R.id.btnArchiveReview);

        tvSubject = findViewById(R.id.tvSubject);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvSchedule = findViewById(R.id.tvSchedule);
        tvRoom = findViewById(R.id.tvRoom);
        tvEmptyDescription = findViewById(R.id.tvEmptyDescription);

        emptyLayout = findViewById(R.id.emptyLayout);

        recyclerTodos = findViewById(R.id.recyclerTodos);
        recyclerNotes = findViewById(R.id.recyclerNotes);
        recyclerFiles = findViewById(R.id.recyclerFiles);
        recyclerLinks = findViewById(R.id.recyclerLinks);

        tabLayout = findViewById(R.id.tabLayout);
        btnAdd = findViewById(R.id.btnAdd);
        btnImportWord = findViewById(R.id.btnImportWord);
        
        tvTodoFilter = findViewById(R.id.tvTodoFilter);
        layoutFilter = findViewById(R.id.layoutFilter);

        databaseHelper = new DatabaseHelper(this);
        
        layoutFilter.setOnClickListener(v -> showFilterMenu());

        recyclerTodos.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerLinks.setLayoutManager(new LinearLayoutManager(this));

        loadReviewDetails();
        setupTabs();

        btnBack.setOnClickListener(v -> finish());

        btnArchiveReview.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Archive Review")
                    .setMessage("Move this review to archive?")
                    .setPositiveButton("Archive", (dialog, which) -> {
                        databaseHelper.archiveItem(DatabaseHelper.TABLE_REVIEW, DatabaseHelper.COL_REVIEW_ID, reviewId);
                        Toast.makeText(this, "Review archived.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnAdd.setOnClickListener(v -> {
            if (selectedTabPosition == 0) {
                showAddTodoDialog();
            } else if (selectedTabPosition == 1) {
                showAddNoteDialog();
            } else if (selectedTabPosition == 2) {
                openFilePicker();
            } else if (selectedTabPosition == 3) {
                showAddLinkDialog();
            }
        });

        btnImportWord.setOnClickListener(v -> {
            wordPickerLauncher.launch(new String[]{
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            });
        });
    }

    private void importFromWord(Uri uri) {
        String content = WordImporter.extractTextFromDocx(this, uri);
        if (content.isEmpty()) {
            Toast.makeText(this, "No text found in document.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = getFileName(uri);
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        boolean inserted = databaseHelper.insertReviewNote(reviewId, fileName, content);
        if (inserted) {
            Toast.makeText(this, "Imported as Note: " + fileName, Toast.LENGTH_SHORT).show();
            loadNotes();
        } else {
            Toast.makeText(this, "Failed to import note.", Toast.LENGTH_SHORT).show();
        }
    }


    // ==========================================================
    // LOAD REVIEW DETAILS
    // ==========================================================

    private void loadReviewDetails() {
        reviewId = getIntent().getIntExtra("reviewId", -1);
        String subject = getIntent().getStringExtra("subject");
        String topic = getIntent().getStringExtra("topic");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String reminder = getIntent().getStringExtra("reminder");

        if (subject == null || subject.isEmpty()) subject = "Subject";
        if (topic == null || topic.isEmpty()) topic = "No topic";
        if (date == null || date.isEmpty()) date = "No date";
        if (time == null || time.isEmpty()) time = "No time";
        if (reminder == null || reminder.isEmpty()) reminder = "No reminder";

        tvSubject.setText(subject);
        tvInstructor.setText("Topic: " + topic);
        tvSchedule.setText(date + " • " + time);
        tvRoom.setText("Reminder: " + reminder);
    }


    // ==========================================================
    // TABS
    // ==========================================================

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("To-do"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));
        tabLayout.addTab(tabLayout.newTab().setText("Links"));

        updateTab(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                updateTab(selectedTabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    // ==========================================================
    // UPDATE TAB
    // ==========================================================

    private void showFilterMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvTodoFilter);
        popup.getMenu().add("Ongoing");
        popup.getMenu().add("Missed");
        popup.getMenu().add("Completed");

        popup.setOnMenuItemClickListener(item -> {
            currentFilter = item.getTitle().toString();
            tvTodoFilter.setText(currentFilter);
            loadTodos();
            return true;
        });
        popup.show();
    }

    private void updateTab(int position) {
        selectedTabPosition = position;
        hideAllContent();
        btnImportWord.setVisibility(View.GONE);
        layoutFilter.setVisibility(View.GONE);

        if (position == 0) {
            btnAdd.setText("+ New To-do");
            layoutFilter.setVisibility(View.VISIBLE);
            loadTodos();
        } else if (position == 1) {
            btnAdd.setText("+ Add Note");
            btnImportWord.setVisibility(View.VISIBLE);
            loadNotes();
        } else if (position == 2) {
            btnAdd.setText("+ Add File");
            loadFiles();
        } else if (position == 3) {
            btnAdd.setText("+ Add Link");
            loadLinks();
        }
    }


    // ==========================================================
    // HIDE EVERYTHING
    // ==========================================================

    private void hideAllContent() {
        recyclerTodos.setVisibility(View.GONE);
        recyclerNotes.setVisibility(View.GONE);
        recyclerFiles.setVisibility(View.GONE);
        recyclerLinks.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        emptyLayout.setVisibility(View.VISIBLE);
        tvEmptyDescription.setText(message);
    }


    // ==========================================================
    // TODO
    // ==========================================================

    private void showAddTodoDialog() {
        if (reviewId == -1) {
            Toast.makeText(this, "Review ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        AddTodoBottomSheet bottomSheet = AddTodoBottomSheet.newInstance(reviewId);
        bottomSheet.setOnTodoAddedListener(this::loadTodos);
        bottomSheet.show(getSupportFragmentManager(), "ADD_TODO");
    }

    private void loadTodos() {
        if (reviewId == -1) {
            showEmptyState("Unable to load to-dos.");
            return;
        }

        todoList = databaseHelper.getTodosByReviewId(reviewId);
        
        // Apply Filter
        ArrayList<Todo> filteredList = new ArrayList<>();
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (Todo t : todoList) {
            boolean isMissed = false;
            if (!t.isCompleted() && t.getDeadline() != null && !t.getDeadline().isEmpty()) {
                try {
                    java.util.Date d = sdf.parse(t.getDeadline());
                    if (d != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        if (now > cal.getTimeInMillis()) isMissed = true;
                    }
                } catch (Exception ignored) {}
            }

            if (currentFilter.equals("Completed") && t.isCompleted()) {
                filteredList.add(t);
            } else if (currentFilter.equals("Missed") && isMissed && !t.isCompleted()) {
                filteredList.add(t);
            } else if (currentFilter.equals("Ongoing") && !t.isCompleted() && !isMissed) {
                filteredList.add(t);
            }
        }

        recyclerTodos.setVisibility(View.GONE);

        if (filteredList.isEmpty()) {
            showEmptyState("No " + currentFilter.toLowerCase() + " to-dos.");
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerTodos.setVisibility(View.VISIBLE);
            todoAdapter = new TodoAdapter(filteredList, this::showViewTodoDialog);
            recyclerTodos.setAdapter(todoAdapter);
        }
    }

    private void showViewTodoDialog(Todo todo) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.brand_ivory));

        // Task Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(todo.getTask());
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
        layout.addView(tvTitle);

        // Status Badge
        TextView tvStatus = new TextView(this);
        tvStatus.setTextSize(14);
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
        tvStatus.setPadding(30, 10, 30, 10);
        tvStatus.setGravity(android.view.Gravity.CENTER);
        
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(0, 20, 0, 30);
        tvStatus.setLayoutParams(statusParams);

        // Determine Status (including Auto-Missed check)
        String status = todo.getStatus();
        if (status == null) status = "Pending";
        
        // Manual check for "Missed" logic here for the dialog
        boolean isMissed = false;
        String deadline = todo.getDeadline();
        if (!todo.isCompleted() && deadline != null && !deadline.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                java.util.Date deadlineDate = sdf.parse(deadline);
                if (deadlineDate != null) {
                    Calendar calDeadline = Calendar.getInstance();
                    calDeadline.setTime(deadlineDate);
                    Calendar calToday = Calendar.getInstance();
                    calToday.set(Calendar.HOUR_OF_DAY, 0);
                    calToday.set(Calendar.MINUTE, 0);
                    calToday.set(Calendar.SECOND, 0);
                    calToday.set(Calendar.MILLISECOND, 0);
                    if (calToday.after(calDeadline)) {
                        isMissed = true;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (isMissed) status = "Missed";

        tvStatus.setText(status);
        if (status.equals("Ongoing") || status.equals("In Progress") || status.equals("Pending")) {
            tvStatus.setBackgroundResource(R.drawable.bg_status_ongoing);
        } else {
            tvStatus.setBackgroundResource(R.drawable.bg_status_white);
        }
        layout.addView(tvStatus);

        // Details Section (Scrollable)
        androidx.core.widget.NestedScrollView scrollView = new androidx.core.widget.NestedScrollView(this);
        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);

        // Subto-dos
        if (todo.getSubTodos() != null && !todo.getSubTodos().isEmpty()) {
            TextView tvSubLabel = new TextView(this);
            tvSubLabel.setText("Subto-dos:");
            tvSubLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            tvSubLabel.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
            tvSubLabel.setAlpha(0.6f);
            detailsLayout.addView(tvSubLabel);

            TextView tvSubValue = new TextView(this);
            tvSubValue.setText(todo.getSubTodos());
            tvSubValue.setTextSize(16);
            tvSubValue.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
            tvSubValue.setPadding(0, 5, 0, 20);
            detailsLayout.addView(tvSubValue);
        }

        // Description
        if (todo.getDescription() != null && !todo.getDescription().isEmpty()) {
            TextView tvDescLabel = new TextView(this);
            tvDescLabel.setText("Description:");
            tvDescLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            tvDescLabel.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
            tvDescLabel.setAlpha(0.6f);
            detailsLayout.addView(tvDescLabel);

            TextView tvDescValue = new TextView(this);
            tvDescValue.setText(todo.getDescription());
            tvDescValue.setTextSize(16);
            tvDescValue.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
            tvDescValue.setPadding(0, 5, 0, 20);
            detailsLayout.addView(tvDescValue);
        }

        scrollView.addView(detailsLayout);
        layout.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));

        // Footer: Deadline
        if (todo.getDeadline() != null && !todo.getDeadline().isEmpty()) {
            TextView tvDeadline = new TextView(this);
            tvDeadline.setText("Deadline: " + todo.getDeadline());
            tvDeadline.setTextSize(14);
            tvDeadline.setPadding(0, 20, 0, 0);
            tvDeadline.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
            tvDeadline.setAlpha(0.6f);
            layout.addView(tvDeadline);
        }

        new AlertDialog.Builder(this, R.style.Theme_Studreminder_Picker)
                .setView(layout)
                .setPositiveButton("Close", null)
                .show();
    }


    // ==========================================================
    // NOTES
    // ==========================================================

    private void showAddNoteDialog() {
        if (reviewId == -1) {
            Toast.makeText(this, "Review ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding / 2, padding, 0);

        EditText inputTitle = new EditText(this);
        inputTitle.setHint("Note title");
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText inputContent = new EditText(this);
        inputContent.setHint("Write your notes here...");
        inputContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputContent.setMinLines(5);
        inputContent.setMaxLines(10);

        layout.addView(inputTitle);
        layout.addView(inputContent);

        new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = inputTitle.getText().toString().trim();
                    String content = inputContent.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter a note title.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (content.isEmpty()) {
                        Toast.makeText(this, "Please enter your note.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean inserted = databaseHelper.insertReviewNote(reviewId, title, content);
                    if (inserted) {
                        Toast.makeText(this, "Note added.", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    } else {
                        Toast.makeText(this, "Failed to add note.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadNotes() {
        if (reviewId == -1) {
            recyclerNotes.setVisibility(View.GONE);
            showEmptyState("Unable to load notes.");
            return;
        }

        reviewNoteList = databaseHelper.getNotesByReviewId(reviewId);
        recyclerNotes.setVisibility(View.GONE);
        recyclerNotes.setAdapter(null);

        if (reviewNoteList.isEmpty()) {
            showEmptyState("Add your first note.");
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerNotes.setVisibility(View.VISIBLE);
            reviewNoteAdapter = new ReviewNoteAdapter(
                    reviewNoteList,
                    (reviewNote, position) -> showDeleteNoteConfirmation(reviewNote),
                    this::showViewNoteDialog
            );
            recyclerNotes.setAdapter(reviewNoteAdapter);
        }
    }

    private void showViewNoteDialog(ReviewNote note) {
        TextView tvTitle = new TextView(this);
        tvTitle.setText(note.getTitle());
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
        tvTitle.setPadding(60, 60, 60, 0);

        TextView tvContent = new TextView(this);
        tvContent.setText(note.getContent());
        tvContent.setTextSize(16);
        tvContent.setTextColor(ContextCompat.getColor(this, R.color.brand_dark_brown));
        tvContent.setPadding(60, 40, 60, 60);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tvTitle);

        androidx.core.widget.NestedScrollView scrollView = new androidx.core.widget.NestedScrollView(this);
        scrollView.addView(tvContent);
        layout.addView(scrollView);

        new AlertDialog.Builder(this, R.style.Theme_Studreminder_Picker)
                .setView(layout)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showDeleteNoteConfirmation(ReviewNote reviewNote) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Delete \"" + reviewNote.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteReviewNote(reviewNote.getId());
                    if (deleted) {
                        Toast.makeText(this, "Note deleted.", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    } else {
                        Toast.makeText(this, "Failed to delete note.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // ==========================================================
    // FILES
    // ==========================================================

    private void openFilePicker() {
        if (reviewId == -1) {
            Toast.makeText(this, "Review ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        filePickerLauncher.launch(new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "image/*",
                "text/plain"
        });
    }

    private void saveSelectedFile(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {}

        String fileName = getFileName(uri);
        boolean inserted = databaseHelper.insertReviewFile(reviewId, fileName, uri.toString());

        if (inserted) {
            Toast.makeText(this, "File added.", Toast.LENGTH_SHORT).show();
            loadFiles();
        } else {
            Toast.makeText(this, "Failed to add file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "Attached File";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return fileName;
    }

    private void loadFiles() {
        if (reviewId == -1) {
            showEmptyState("Unable to load files.");
            return;
        }

        reviewFileList = databaseHelper.getFilesByReviewId(reviewId);
        recyclerFiles.setVisibility(View.GONE);

        if (reviewFileList.isEmpty()) {
            showEmptyState("Add your first file.");
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerFiles.setVisibility(View.VISIBLE);
            reviewFileAdapter = new ReviewFileAdapter(reviewFileList, (reviewFile, position) -> showDeleteFileConfirmation(reviewFile));
            recyclerFiles.setAdapter(reviewFileAdapter);
        }
    }

    private void showDeleteFileConfirmation(ReviewFile reviewFile) {
        new AlertDialog.Builder(this)
                .setTitle("Delete File")
                .setMessage("Remove \"" + reviewFile.getFileName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteReviewFile(reviewFile.getId());
                    if (deleted) {
                        Toast.makeText(this, "File removed.", Toast.LENGTH_SHORT).show();
                        loadFiles();
                    } else {
                        Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // ==========================================================
    // LINKS
    // ==========================================================

    private void showAddLinkDialog() {
        if (reviewId == -1) {
            Toast.makeText(this, "Review ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding / 2, padding, 0);

        EditText inputTitle = new EditText(this);
        inputTitle.setHint("Link title");
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText inputUrl = new EditText(this);
        inputUrl.setHint("URL (https://...)");
        inputUrl.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        layout.addView(inputTitle);
        layout.addView(inputUrl);

        new AlertDialog.Builder(this)
                .setTitle("Add Link")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = inputTitle.getText().toString().trim();
                    String url = inputUrl.getText().toString().trim();

                    if (title.isEmpty() || url.isEmpty()) {
                        Toast.makeText(this, "Please enter both title and URL.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean inserted = databaseHelper.insertReviewLink(reviewId, title, url);
                    if (inserted) {
                        Toast.makeText(this, "Link added.", Toast.LENGTH_SHORT).show();
                        loadLinks();
                    } else {
                        Toast.makeText(this, "Failed to add link.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadLinks() {
        if (reviewId == -1) {
            showEmptyState("Unable to load links.");
            return;
        }

        reviewLinkList = databaseHelper.getLinksByReviewId(reviewId);
        recyclerLinks.setVisibility(View.GONE);

        if (reviewLinkList.isEmpty()) {
            showEmptyState("Add your first link.");
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerLinks.setVisibility(View.VISIBLE);
            reviewLinkAdapter = new ReviewLinkAdapter(reviewLinkList, (reviewLink, position) -> showDeleteLinkConfirmation(reviewLink));
            recyclerLinks.setAdapter(reviewLinkAdapter);
        }
    }

    private void showDeleteLinkConfirmation(ReviewLink reviewLink) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Link")
                .setMessage("Remove \"" + reviewLink.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteReviewLink(reviewLink.getId());
                    if (deleted) {
                        Toast.makeText(this, "Link removed.", Toast.LENGTH_SHORT).show();
                        loadLinks();
                    } else {
                        Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}