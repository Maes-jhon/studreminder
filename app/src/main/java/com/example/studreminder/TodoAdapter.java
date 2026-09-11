package com.example.studreminder;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter
        extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final List<Todo> todoList;
    private final OnTodoClickListener clickListener;

    public interface OnTodoClickListener {
        void onClick(Todo todo);
    }

    public TodoAdapter(List<Todo> todoList, OnTodoClickListener clickListener) {
        this.todoList = todoList;
        this.clickListener = clickListener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkTodo;
        TextView tvTodoTask, tvTodoLabel, tvStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkTodo = itemView.findViewById(R.id.checkTodo);
            tvTodoTask = itemView.findViewById(R.id.tvTodoTask);
            tvTodoLabel = itemView.findViewById(R.id.tvTodoLabel);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
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
        
        String label = todo.getLabel();
        if (label == null || label.isEmpty()) label = "General";
        holder.tvTodoLabel.setText(label);
        
        // Status Logic
        String status = todo.isCompleted() ? "Completed" : "Ongoing";
        if (!todo.isCompleted() && isPastDeadline(todo.getDeadline())) {
            status = "Missed";
        }
        
        updateStatusBadge(holder.tvStatusBadge, status);

        holder.checkTodo.setOnCheckedChangeListener(null);
        holder.checkTodo.setChecked(todo.isCompleted());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(todo);
            }
        });

        updateTaskStyle(
                holder.tvTodoTask,
                todo.isCompleted()
        );

        holder.checkTodo.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    todo.setCompleted(isChecked);
                    updateTaskStyle(
                            holder.tvTodoTask,
                            isChecked
                    );

                    db.updateTodoCompleted(todo.getId(), isChecked);
                    
                    // Refresh status badge
                    String newStatus = isChecked ? "Completed" : (isPastDeadline(todo.getDeadline()) ? "Missed" : "Ongoing");
                    updateStatusBadge(holder.tvStatusBadge, newStatus);

                    if (isChecked) {
                        XPManager xp = new XPManager(holder.itemView.getContext());
                        boolean rankUp = xp.addXP(10);
                        Toast.makeText(holder.itemView.getContext(), "+10 XP! Mission Completed!", Toast.LENGTH_SHORT).show();
                        
                        if (rankUp && holder.itemView.getContext() instanceof MainActivity) {
                            ((MainActivity) holder.itemView.getContext()).showPromotionDialog(xp.getRank());
                        }
                    }
                }
        );
    }

    private boolean isPastDeadline(String deadline) {
        if (deadline == null || deadline.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date deadlineDate = sdf.parse(deadline);
            if (deadlineDate == null) return false;

            Calendar calDeadline = Calendar.getInstance();
            calDeadline.setTime(deadlineDate);
            calDeadline.set(Calendar.HOUR_OF_DAY, 23);
            calDeadline.set(Calendar.MINUTE, 59);

            return Calendar.getInstance().after(calDeadline);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateStatusBadge(TextView badge, String status) {
        badge.setText(status);
        switch (status) {
            case "Completed":
                badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                badge.setAlpha(0.6f);
                break;
            case "Missed":
                badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_simple, 0, 0, 0);
                badge.setTextColor(Color.parseColor("#E53935")); // Red
                badge.setAlpha(1.0f);
                break;
            default: // Ongoing
                badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_simple, 0, 0, 0);
                badge.setTextColor(Color.parseColor("#343B1B")); // Dark Brown
                badge.setAlpha(1.0f);
                break;
        }
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
