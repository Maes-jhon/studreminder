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

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View viewColor;
        TextView tvSubject;
        TextView tvDay;
        TextView tvTime;
        TextView tvRoom;
        ImageButton btnArchiveSchedule, btnDeleteSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewColor = itemView.findViewById(R.id.viewColor);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            btnArchiveSchedule = itemView.findViewById(R.id.btnArchiveSchedule);
            btnDeleteSchedule = itemView.findViewById(R.id.btnDeleteSchedule);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Schedule schedule = scheduleList.get(position);

        holder.tvSubject.setText(schedule.getSubject());

        // Remove weekday (e.g. "Friday, ")
        String fullDate = schedule.getDate();

        if (fullDate != null && fullDate.contains(",")) {

            int firstComma = fullDate.indexOf(",");

            holder.tvDay.setText(
                    fullDate.substring(firstComma + 2)
            );

        } else {

            holder.tvDay.setText(fullDate);

        }

        holder.tvTime.setText(
                "🕒 "
                        + schedule.getStartTime()
                        + " - "
                        + schedule.getEndTime()
        );

        holder.tvRoom.setText(
                "📍 "
                        + schedule.getRoom()
        );

        holder.viewColor.setBackgroundColor(
                schedule.getColor()
        );

        holder.btnArchiveSchedule.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());
            db.archiveItem(DatabaseHelper.TABLE_SCHEDULE, DatabaseHelper.COL_ID, schedule.getId());
            scheduleList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "Item archived", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeleteSchedule.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());
            db.deleteSchedule(schedule.getId());
            scheduleList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "Schedule Deleted", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(holder.itemView.getContext());

            builder.setTitle("Schedule Options");

            builder.setMessage(
                    schedule.getSubject()
                            + "\n"
                            + schedule.getDate()
            );

            builder.setPositiveButton(
                    "Edit Schedule",
                    (dialog, which) -> {
                        Intent intent = new Intent(holder.itemView.getContext(), AddScheduleActivity.class);
                        intent.putExtra("schedule_id", schedule.getId());
                        intent.putExtra("subject", schedule.getSubject());
                        intent.putExtra("date", schedule.getDate());
                        intent.putExtra("start_time", schedule.getStartTime());
                        intent.putExtra("end_time", schedule.getEndTime());
                        intent.putExtra("room", schedule.getRoom());
                        intent.putExtra("reminder", schedule.getReminder());
                        holder.itemView.getContext().startActivity(intent);
                    });

            builder.setNeutralButton(
                    "Delete",
                    (dialog, which) -> {

                        DatabaseHelper db =
                                new DatabaseHelper(holder.itemView.getContext());

                        boolean deleted =
                                db.deleteSchedule(schedule.getId());

                        if (deleted) {

                            scheduleList.remove(position);

                            notifyItemRemoved(position);

                            Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Schedule Deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

            builder.setNegativeButton("Cancel", null);

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}