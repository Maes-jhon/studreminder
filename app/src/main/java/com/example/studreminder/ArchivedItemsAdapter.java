package com.example.studreminder;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArchivedItemsAdapter extends RecyclerView.Adapter<ArchivedItemsAdapter.ViewHolder> {

    private List<ArchivedItemModel> archivedList;
    private DatabaseHelper db;

    public ArchivedItemsAdapter(List<ArchivedItemModel> archivedList) {
        this.archivedList = archivedList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ImageButton btnRestore, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvArchivedTitle);
            tvSubtitle = itemView.findViewById(R.id.tvArchivedSubtitle);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnDelete = itemView.findViewById(R.id.btnPermanentDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = new DatabaseHelper(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archived_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArchivedItemModel item = archivedList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());

        holder.btnRestore.setOnClickListener(v -> {
            boolean success = db.unarchiveItem(item.getTableName(), item.getIdColumn(), item.getId());
            if (success) {
                Toast.makeText(holder.itemView.getContext(), "Item restored", Toast.LENGTH_SHORT).show();
                archivedList.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Permanent Delete")
                    .setMessage("Are you sure you want to permanently delete this item? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean success = db.permanentlyDeleteItem(item.getTableName(), item.getIdColumn(), item.getId());
                        if (success) {
                            Toast.makeText(holder.itemView.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                            archivedList.remove(position);
                            notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return archivedList.size();
    }
}