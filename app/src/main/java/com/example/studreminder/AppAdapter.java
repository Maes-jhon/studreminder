package com.example.studreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppModel> appList;
    private Context context;
    private SharedPreferences prefs;

    public AppAdapter(Context context, List<AppModel> appList) {
        this.context = context;
        this.appList = appList;
        this.prefs = context.getSharedPreferences("locker_prefs", Context.MODE_PRIVATE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName, tvPackageName;
        SwitchMaterial switchLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            switchLock = itemView.findViewById(R.id.switchLock);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = appList.get(position);
        holder.ivAppIcon.setImageDrawable(app.getIcon());
        holder.tvAppName.setText(app.getAppName());
        holder.tvPackageName.setText(app.getPackageName());

        holder.switchLock.setOnCheckedChangeListener(null);
        holder.switchLock.setChecked(app.isLocked());

        holder.switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setLocked(isChecked);
            updateBlocklist(app.getPackageName(), isChecked);
        });
    }

    private void updateBlocklist(String packageName, boolean isLocked) {
        Set<String> currentLocked = new HashSet<>(prefs.getStringSet("LOCKED_APPS_SET", new HashSet<>()));
        if (isLocked) {
            currentLocked.add(packageName);
        } else {
            currentLocked.remove(packageName);
        }

        // Save to SharedPreferences asynchronously
        prefs.edit().putStringSet("LOCKED_APPS_SET", currentLocked).apply();

        // Sync changes live if a focus session is active
        if (FocusState.isSessionActive(context)) {
            FocusState.updateSessionApps(context, currentLocked);
        }
    }

    public void updateList(List<AppModel> newList) {
        this.appList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}