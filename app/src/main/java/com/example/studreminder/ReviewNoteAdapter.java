package com.example.studreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewNoteAdapter
        extends RecyclerView.Adapter<ReviewNoteAdapter.ViewHolder> {

    private final List<ReviewNote> noteList;
    private final OnNoteDeleteListener deleteListener;
    private final OnNoteClickListener clickListener;

    public interface OnNoteDeleteListener {

        void onDelete(
                ReviewNote reviewNote,
                int position
        );
    }

    public interface OnNoteClickListener {
        void onClick(ReviewNote note);
    }

    public ReviewNoteAdapter(
            List<ReviewNote> noteList,
            OnNoteDeleteListener deleteListener,
            OnNoteClickListener clickListener
    ) {

        this.noteList = noteList;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNoteTitle;
        TextView tvNoteContent;
        ImageButton btnDeleteNote;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvNoteTitle =
                    itemView.findViewById(
                            R.id.tvNoteTitle
                    );

            tvNoteContent =
                    itemView.findViewById(
                            R.id.tvNoteContent
                    );

            btnDeleteNote =
                    itemView.findViewById(
                            R.id.btnDeleteNote
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
                                R.layout.item_review_note,
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

        ReviewNote reviewNote =
                noteList.get(position);

        holder.tvNoteTitle.setText(
                reviewNote.getTitle()
        );

        holder.tvNoteContent.setText(
                reviewNote.getContent()
        );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(reviewNote);
            }
        });

        holder.btnDeleteNote
                .setOnClickListener(v -> {

                    int currentPosition =
                            holder.getBindingAdapterPosition();

                    if (currentPosition ==
                            RecyclerView.NO_POSITION) {
                        return;
                    }

                    if (deleteListener != null) {

                        deleteListener.onDelete(
                                noteList.get(currentPosition),
                                currentPosition
                        );
                    }
                });
    }

    @Override
    public int getItemCount() {

        return noteList.size();
    }
}