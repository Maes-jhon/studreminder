package com.example.studreminder;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddTodoBottomSheet extends BottomSheetDialogFragment {

    private EditText etTask, etCustomLabel, etSubTodos, etDesc;
    private TextView btnLabelCustom, btnLabelSubjects, tvSelectedSubject, tvDeadlineValue;
    private LinearLayout layoutSubjectPicker;
    private SwitchMaterial switchDeadline;
    private Button btnCreate;

    private boolean isCustomLabel = true;
    private String selectedSubject = "";
    private String selectedDeadline = "";
    private int reviewId = -1;

    private DatabaseHelper databaseHelper;
    private OnTodoAddedListener listener;

    public interface OnTodoAddedListener {
        void onAdded();
    }

    public static AddTodoBottomSheet newInstance(int reviewId) {
        AddTodoBottomSheet fragment = new AddTodoBottomSheet();
        Bundle args = new Bundle();
        args.putInt("reviewId", reviewId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTodoAddedListener(OnTodoAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_todo_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        if (getArguments() != null) {
            reviewId = getArguments().getInt("reviewId", -1);
        }

        etTask = view.findViewById(R.id.etTodoTask);
        etCustomLabel = view.findViewById(R.id.etCustomLabel);
        etSubTodos = view.findViewById(R.id.etSubTodos);
        etDesc = view.findViewById(R.id.etTodoDesc);

        btnLabelCustom = view.findViewById(R.id.btnLabelCustom);
        btnLabelSubjects = view.findViewById(R.id.btnLabelSubjects);
        tvSelectedSubject = view.findViewById(R.id.tvSelectedSubject);
        tvDeadlineValue = view.findViewById(R.id.tvDeadlineValue);
        layoutSubjectPicker = view.findViewById(R.id.layoutSubjectPicker);
        switchDeadline = view.findViewById(R.id.switchDeadline);
        btnCreate = view.findViewById(R.id.btnCreateTodo);

        setupLabelToggle();
        setupDeadline();
        setupValidation();

        layoutSubjectPicker.setOnClickListener(v -> showSubjectPickerDialog());

        btnCreate.setOnClickListener(v -> saveTodo());
    }

    private void setupLabelToggle() {
        btnLabelCustom.setOnClickListener(v -> {
            isCustomLabel = true;
            btnLabelCustom.setBackgroundResource(R.drawable.brutal_box_yellow);
            btnLabelSubjects.setBackgroundResource(R.drawable.brutal_box_white);
            etCustomLabel.setVisibility(View.VISIBLE);
            layoutSubjectPicker.setVisibility(View.GONE);
        });

        btnLabelSubjects.setOnClickListener(v -> {
            isCustomLabel = false;
            btnLabelSubjects.setBackgroundResource(R.drawable.brutal_box_yellow);
            btnLabelCustom.setBackgroundResource(R.drawable.brutal_box_white);
            etCustomLabel.setVisibility(View.GONE);
            layoutSubjectPicker.setVisibility(View.VISIBLE);
        });
    }

    private void showSubjectPickerDialog() {
        ArrayList<Subject> subjects = databaseHelper.getAllSubjects();
        if (subjects.isEmpty()) {
            Toast.makeText(requireContext(), "No subjects available. Create one first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] subjectNames = new String[subjects.size()];
        for (int i = 0; i < subjects.size(); i++) {
            subjectNames[i] = subjects.get(i).getSubject();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Select a Subject")
                .setItems(subjectNames, (dialog, which) -> {
                    selectedSubject = subjectNames[which];
                    tvSelectedSubject.setText(selectedSubject);
                    tvSelectedSubject.setAlpha(1.0f);
                    validate();
                })
                .show();
    }

    private void setupDeadline() {
        switchDeadline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showDatePicker();
            } else {
                selectedDeadline = "";
                tvDeadlineValue.setVisibility(View.GONE);
            }
        });

        tvDeadlineValue.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Deadline")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.BrandDatePicker)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedDeadline = format.format(calendar.getTime());
            tvDeadlineValue.setText(selectedDeadline);
            tvDeadlineValue.setVisibility(View.VISIBLE);
            tvDeadlineValue.setAlpha(1.0f);
        });

        datePicker.addOnNegativeButtonClickListener(v -> {
            if (selectedDeadline.isEmpty()) switchDeadline.setChecked(false);
        });

        datePicker.show(getParentFragmentManager(), "DEADLINE_PICKER");
    }

    private void setupValidation() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etTask.addTextChangedListener(watcher);
    }

    private void validate() {
        String task = etTask.getText().toString().trim();
        boolean isValid = !task.isEmpty();
        
        if (isValid) {
            btnCreate.setEnabled(true);
            btnCreate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.true_black));
            btnCreate.setTextColor(Color.WHITE);
        } else {
            btnCreate.setEnabled(false);
            btnCreate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
            btnCreate.setTextColor(Color.LTGRAY);
        }
    }

    private void saveTodo() {
        String task = etTask.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String sub = etSubTodos.getText().toString().trim();
        String label = isCustomLabel ? etCustomLabel.getText().toString().trim() : selectedSubject;
        String labelType = isCustomLabel ? "CUSTOM" : "SUBJECT";

        if (label.isEmpty() && !isCustomLabel) {
            Toast.makeText(requireContext(), "Please select a subject.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = databaseHelper.insertTodo(reviewId, task, desc, sub, selectedDeadline, label, labelType);
        if (inserted) {
            if (listener != null) listener.onAdded();
            dismiss();
        } else {
            Toast.makeText(requireContext(), "Failed to add to-do.", Toast.LENGTH_SHORT).show();
        }
    }
}
