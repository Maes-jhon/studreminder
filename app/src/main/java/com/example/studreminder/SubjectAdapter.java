package com.example.studreminder;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter
        extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private final List<Subject> subjectList;

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        View viewColor;
        TextView tvSubject;
        TextView tvTeacher;
        TextView tvRoom;
        ImageButton btnArchiveSubject, btnDeleteSubject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewColor =
                    itemView.findViewById(
                            R.id.viewColor
                    );

            tvSubject =
                    itemView.findViewById(
                            R.id.tvSubject
                    );

            tvTeacher =
                    itemView.findViewById(
                            R.id.tvTeacher
                    );

            tvRoom =
                    itemView.findViewById(
                            R.id.tvRoom
                    );

            btnArchiveSubject =
                    itemView.findViewById(
                            R.id.btnArchiveSubject
                    );

            btnDeleteSubject =
                    itemView.findViewById(
                            R.id.btnDeleteSubject
                    );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_subject,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Subject subject =
                subjectList.get(position);


        // SUBJECT NAME
        holder.tvSubject.setText(
                subject.getSubject()
        );


        // TEACHER
        holder.tvTeacher.setText(
                "Instructor: "
                        + subject.getTeacher()
        );


        // ROOM
        holder.tvRoom.setText(
                "Room: "
                        + subject.getRoom()
        );


        holder.viewColor.setBackgroundColor(
                subject.getColor()
        );

        holder.btnArchiveSubject.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(holder.itemView.getContext());
            databaseHelper.archiveItem(DatabaseHelper.TABLE_SUBJECT, DatabaseHelper.COL_SUBJECT_ID, subject.getId());
            subjectList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "Item archived", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeleteSubject.setOnClickListener(v -> {
            showDeleteConfirmation(holder, subject, position);
        });


        // ==========================================================
        // TAP: OPEN SUBJECT DETAILS
        // ==========================================================

        holder.itemView.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return;
            }

            Subject selectedSubject =
                    subjectList.get(currentPosition);

            String[] options = {"View Details", "Edit Subject"};
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle(selectedSubject.getSubject())
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(holder.itemView.getContext(), SubjectDetailsActivity.class);
                            intent.putExtra("subject", selectedSubject.getSubject());
                            intent.putExtra("teacher", selectedSubject.getTeacher());
                            intent.putExtra("room", selectedSubject.getRoom());
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(holder.itemView.getContext(), AddSubjectActivity.class);
                            intent.putExtra("subject_id", selectedSubject.getId());
                            intent.putExtra("subject_name", selectedSubject.getSubject());
                            intent.putExtra("subject_teacher", selectedSubject.getTeacher());
                            intent.putExtra("subject_room", selectedSubject.getRoom());
                            holder.itemView.getContext().startActivity(intent);
                        }
                    })
                    .show();
        });


        // ==========================================================
        // LONG PRESS: DELETE SUBJECT
        // ==========================================================

        holder.itemView.setOnLongClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return true;
            }

            Subject selectedSubject =
                    subjectList.get(currentPosition);

            new AlertDialog.Builder(
                    holder.itemView.getContext()
            )

                    .setTitle(
                            "Delete Subject"
                    )

                    .setMessage(
                            "Are you sure you want to delete \""
                                    + selectedSubject.getSubject()
                                    + "\"?"
                    )

                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                DatabaseHelper databaseHelper =
                                        new DatabaseHelper(
                                                holder.itemView.getContext()
                                        );

                                boolean deleted =
                                        databaseHelper.deleteSubject(
                                                selectedSubject.getId()
                                        );

                                if (deleted) {

                                    subjectList.remove(
                                            currentPosition
                                    );

                                    notifyItemRemoved(
                                            currentPosition
                                    );

                                    Toast.makeText(
                                            holder.itemView.getContext(),
                                            "Subject Deleted",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            holder.itemView.getContext(),
                                            "Failed to delete subject",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )

                    .setNegativeButton(
                            "Cancel",
                            null
                    )

                    .show();

            return true;
        });
    }

    private void showDeleteConfirmation(ViewHolder holder, Subject selectedSubject, int position) {
        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete \"" + selectedSubject.getSubject() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    DatabaseHelper databaseHelper = new DatabaseHelper(holder.itemView.getContext());
                    boolean deleted = databaseHelper.deleteSubject(selectedSubject.getId());
                    if (deleted) {
                        subjectList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(holder.itemView.getContext(), "Subject Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to delete subject", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}