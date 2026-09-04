package com.example.studreminder;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter
        extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final List<Todo> todoList;

    public TodoAdapter(List<Todo> todoList) {
        this.todoList = todoList;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkTodo;
        TextView tvTodoTask, tvTodoDesc, tvTodoDeadline, tvStatusBadge;
        ImageButton btnArchiveTodo, btnDeleteTodo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkTodo = itemView.findViewById(R.id.checkTodo);
            tvTodoTask = itemView.findViewById(R.id.tvTodoTask);
            tvTodoDesc = itemView.findViewById(R.id.tvTodoDesc);
            tvTodoDeadline = itemView.findViewById(R.id.tvTodoDeadline);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnArchiveTodo = itemView.findViewById(R.id.btnArchiveTodo);
            btnDeleteTodo = itemView.findViewById(R.id.btnDeleteTodo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_todo,
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

        Todo todo = todoList.get(position);
        DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());

        holder.tvTodoTask.setText(todo.getTask());
        holder.tvTodoDesc.setText(todo.getDescription());
        holder.tvTodoDeadline.setText("Deadline: " + todo.getDeadline());

        String status = todo.getStatus();
        if (status == null) status = "Pending";

        // Auto-Missed Logic
        if (!todo.isCompleted() && isPastDeadline(todo.getDeadline())) {
            status = "Missed";
        }

        holder.tvStatusBadge.setText(status);
        updateStatusBadgeUI(holder.tvStatusBadge, status);

        holder.checkTodo.setOnCheckedChangeListener(null);
        holder.checkTodo.setChecked(todo.isCompleted());

        updateTaskStyle(
                holder.tvTodoTask,
                todo.isCompleted()
        );

        holder.checkTodo.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    todo.setCompleted(isChecked);
                    String newStatus = isChecked ? "Completed" : "Pending";
                    todo.setStatus(newStatus);

                    updateTaskStyle(
                            holder.tvTodoTask,
                            isChecked
                    );

                    holder.tvStatusBadge.setText(newStatus);
                    updateStatusBadgeUI(holder.tvStatusBadge, newStatus);

                    db.updateTodoStatus(todo.getId(), newStatus);

                    if (isChecked) {
                        XPManager xp = new XPManager(holder.itemView.getContext());
                        xp.addXP(10);
                        Toast.makeText(holder.itemView.getContext(), "+10 XP! Keep going!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        holder.tvStatusBadge.setOnClickListener(v -> {
            if (todo.isCompleted()) return;

            String currentStatus = todo.getStatus();
            String nextStatus = "Pending";

            if (currentStatus.equals("Pending")) nextStatus = "In Progress";
            else if (currentStatus.equals("In Progress")) nextStatus = "Completed";

            todo.setStatus(nextStatus);
            holder.tvStatusBadge.setText(nextStatus);
            updateStatusBadgeUI(holder.tvStatusBadge, nextStatus);

            if (nextStatus.equals("Completed")) {
                todo.setCompleted(true);
                holder.checkTodo.setChecked(true);
                updateTaskStyle(holder.tvTodoTask, true);
            }

            db.updateTodoStatus(todo.getId(), nextStatus);
        });

        holder.btnArchiveTodo.setOnClickListener(v -> {
            db.archiveItem(DatabaseHelper.TABLE_TODO, DatabaseHelper.COL_TODO_ID, todo.getId());
            todoList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "Item archived", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeleteTodo.setOnClickListener(v -> {
            db.deleteTodo(todo.getId());
            todoList.remove(position);
            notifyItemRemoved(position);
        });
    }

    private boolean isPastDeadline(String deadline) {
        if (deadline == null || deadline.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date deadlineDate = sdf.parse(deadline);
            if (deadlineDate == null) return false;

            Calendar calDeadline = Calendar.getInstance();
            calDeadline.setTime(deadlineDate);

            Calendar calToday = Calendar.getInstance();
            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);
            calToday.set(Calendar.SECOND, 0);
            calToday.set(Calendar.MILLISECOND, 0);

            return calToday.after(calDeadline);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateStatusBadgeUI(TextView badge, String status) {
        int color;
        switch (status) {
            case "In Progress":
                color = ContextCompat.getColor(badge.getContext(), R.color.brand_celtic_blue);
                break;
            case "Completed":
                color = ContextCompat.getColor(badge.getContext(), R.color.brand_tea_green);
                break;
            case "Missed":
                color = ContextCompat.getColor(badge.getContext(), R.color.accent_red);
                break;
            default: // Pending
                color = ContextCompat.getColor(badge.getContext(), R.color.accent_yellow);
                break;
        }
        badge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
    }

    private void updateTaskStyle(
            TextView textView,
            boolean completed
    ) {

        if (completed) {

            textView.setPaintFlags(
                    textView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            textView.setAlpha(0.55f);

        } else {

            textView.setPaintFlags(
                    textView.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );

            textView.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }
}