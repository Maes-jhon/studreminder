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

public class ReviewAdapter
        extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvReviewTopic;
        TextView tvReviewDateTime;
        TextView tvReviewReminder;
        TextView tvReviewStatus;
        ImageButton btnArchiveReview, btnDeleteReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReviewTopic =
                    itemView.findViewById(
                            R.id.tvReviewTopic
                    );

            tvReviewDateTime =
                    itemView.findViewById(
                            R.id.tvReviewDateTime
                    );

            tvReviewReminder =
                    itemView.findViewById(
                            R.id.tvReviewReminder
                    );

            tvReviewStatus =
                    itemView.findViewById(
                            R.id.tvReviewStatus
                    );

            btnArchiveReview =
                    itemView.findViewById(
                            R.id.btnArchiveReview
                    );

            btnDeleteReview =
                    itemView.findViewById(
                            R.id.btnDeleteReview
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
                                R.layout.item_review,
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

        Review review =
                reviewList.get(position);

        holder.tvReviewTopic.setText(
                review.getTopic()
        );

        holder.tvReviewDateTime.setText(
                review.getDate()
                        + " • "
                        + review.getTime()
        );

        holder.tvReviewReminder.setText(
                "Reminder: "
                        + review.getReminder()
        );

        holder.tvReviewStatus.setText(
                "Status: "
                        + (review.getStatus() != null ? review.getStatus() : "Upcoming")
        );

        holder.btnArchiveReview.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());
            db.archiveItem(DatabaseHelper.TABLE_REVIEW, DatabaseHelper.COL_REVIEW_ID, review.getId());
            reviewList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "Item archived", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeleteReview.setOnClickListener(v -> {
            showDeleteConfirmation(holder, review);
        });

        // NORMAL TAP: OPEN REVIEW DETAILS
        holder.itemView.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return;
            }

            Review selectedReview =
                    reviewList.get(currentPosition);

            Intent intent =
                    new Intent(
                            holder.itemView.getContext(),
                            ReviewDetailsActivity.class
                    );

            intent.putExtra(
                    "reviewId",
                    selectedReview.getId()
            );

            intent.putExtra(
                    "subject",
                    selectedReview.getSubject()
            );

            intent.putExtra(
                    "topic",
                    selectedReview.getTopic()
            );

            intent.putExtra(
                    "date",
                    selectedReview.getDate()
            );

            intent.putExtra(
                    "time",
                    selectedReview.getTime()
            );

            intent.putExtra(
                    "reminder",
                    selectedReview.getReminder()
            );

            holder.itemView
                    .getContext()
                    .startActivity(intent);
        });

        // LONG PRESS: EDIT OR DELETE
        holder.itemView.setOnLongClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return true;
            }

            Review selectedReview =
                    reviewList.get(currentPosition);

            String[] options = {
                    "Edit Review",
                    "Delete Review"
            };

            new AlertDialog.Builder(
                    holder.itemView.getContext()
            )
                    .setTitle(
                            selectedReview.getTopic()
                    )
                    .setItems(
                            options,
                            (dialog, which) -> {

                                if (which == 0) {

                                    Intent intent =
                                            new Intent(
                                                    holder.itemView.getContext(),
                                                    AddReviewActivity.class
                                            );

                                    intent.putExtra(
                                            "editMode",
                                            true
                                    );

                                    intent.putExtra(
                                            "reviewId",
                                            selectedReview.getId()
                                    );

                                    intent.putExtra(
                                            "subject",
                                            selectedReview.getSubject()
                                    );

                                    intent.putExtra(
                                            "topic",
                                            selectedReview.getTopic()
                                    );

                                    intent.putExtra(
                                            "date",
                                            selectedReview.getDate()
                                    );

                                    intent.putExtra(
                                            "time",
                                            selectedReview.getTime()
                                    );

                                    intent.putExtra(
                                            "reminder",
                                            selectedReview.getReminder()
                                    );

                                    holder.itemView
                                            .getContext()
                                            .startActivity(intent);

                                } else if (which == 1) {

                                    showDeleteConfirmation(
                                            holder,
                                            selectedReview
                                    );
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

    private void showDeleteConfirmation(
            ViewHolder holder,
            Review selectedReview
    ) {

        new AlertDialog.Builder(
                holder.itemView.getContext()
        )
                .setTitle("Delete Review")
                .setMessage(
                        "Delete \""
                                + selectedReview.getTopic()
                                + "\"?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int currentPosition =
                                    holder.getBindingAdapterPosition();

                            if (currentPosition ==
                                    RecyclerView.NO_POSITION) {
                                return;
                            }

                            DatabaseHelper databaseHelper =
                                    new DatabaseHelper(
                                            holder.itemView.getContext()
                                    );

                            boolean deleted =
                                    databaseHelper.deleteReview(
                                            selectedReview.getId()
                                    );

                            if (deleted) {

                                reviewList.remove(
                                        currentPosition
                                );

                                notifyItemRemoved(
                                        currentPosition
                                );

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Review Deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Delete failed",
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
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}