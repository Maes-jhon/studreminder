package com.example.studreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.ViewHolder> {

    private final ArrayList<PerformanceData> dataList;

    public PerformanceAdapter(ArrayList<PerformanceData> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayStats;
        CircularProgressIndicator progressDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayStats = itemView.findViewById(R.id.tvDayStats);
            progressDay = itemView.findViewById(R.id.progressDay);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PerformanceData data = dataList.get(position);
        holder.tvDayName.setText(data.getDay());
        
        String stats = data.getTasksCompleted() + " Tasks • " + (data.getDurationMinutes() / 60) + "h " + (data.getDurationMinutes() % 60) + "m";
        holder.tvDayStats.setText(stats);

        // Simple progress calculation (max 10 tasks for 100%)
        int progress = (int) ((data.getTasksCompleted() / 10.0) * 100);
        holder.progressDay.setProgress(Math.min(progress, 100));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
