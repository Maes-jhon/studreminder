package com.example.studreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewStudySetAdapter
        extends RecyclerView.Adapter<ReviewStudySetAdapter.ViewHolder> {

    private final List<ReviewStudySet> studySetList;
    private final OnStudySetDeleteListener deleteListener;

    public interface OnStudySetDeleteListener {

        void onDelete(
                ReviewStudySet studySet,
                int position
        );
    }

    public ReviewStudySetAdapter(
            List<ReviewStudySet> studySetList,
            OnStudySetDeleteListener deleteListener
    ) {

        this.studySetList = studySetList;
        this.deleteListener = deleteListener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvStudyQuestion;
        TextView tvStudyAnswer;
        ImageButton btnDeleteStudySet;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvStudyQuestion =
                    itemView.findViewById(
                            R.id.tvStudyQuestion
                    );

            tvStudyAnswer =
                    itemView.findViewById(
                            R.id.tvStudyAnswer
                    );

            btnDeleteStudySet =
                    itemView.findViewById(
                            R.id.btnDeleteStudySet
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
                                R.layout.item_review_study_set,
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

        ReviewStudySet studySet =
                studySetList.get(position);

        holder.tvStudyQuestion.setText(
                studySet.getQuestion()
        );

        holder.tvStudyAnswer.setText(
                studySet.getAnswer()
        );


        holder.btnDeleteStudySet
                .setOnClickListener(v -> {

                    int currentPosition =
                            holder.getBindingAdapterPosition();

                    if (currentPosition ==
                            RecyclerView.NO_POSITION) {
                        return;
                    }

                    if (deleteListener != null) {

                        deleteListener.onDelete(
                                studySetList.get(currentPosition),
                                currentPosition
                        );
                    }
                });
    }

    @Override
    public int getItemCount() {

        return studySetList.size();
    }
}