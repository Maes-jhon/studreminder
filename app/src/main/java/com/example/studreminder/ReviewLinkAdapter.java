package com.example.studreminder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewLinkAdapter
        extends RecyclerView.Adapter<ReviewLinkAdapter.ViewHolder> {

    private final List<ReviewLink> linkList;
    private final OnLinkDeleteListener deleteListener;

    public interface OnLinkDeleteListener {
        void onDelete(
                ReviewLink reviewLink,
                int position
        );
    }

    public ReviewLinkAdapter(
            List<ReviewLink> linkList,
            OnLinkDeleteListener deleteListener
    ) {

        this.linkList = linkList;
        this.deleteListener = deleteListener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvLinkTitle;
        TextView tvLinkUrl;
        ImageButton btnDeleteLink;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvLinkTitle =
                    itemView.findViewById(
                            R.id.tvLinkTitle
                    );

            tvLinkUrl =
                    itemView.findViewById(
                            R.id.tvLinkUrl
                    );

            btnDeleteLink =
                    itemView.findViewById(
                            R.id.btnDeleteLink
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
                                R.layout.item_review_link,
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

        ReviewLink reviewLink =
                linkList.get(position);

        holder.tvLinkTitle.setText(
                reviewLink.getTitle()
        );

        holder.tvLinkUrl.setText(
                reviewLink.getUrl()
        );


        // OPEN LINK
        holder.itemView.setOnClickListener(v -> {

            String url =
                    reviewLink.getUrl().trim();

            if (!url.startsWith("http://")
                    && !url.startsWith("https://")) {

                url = "https://" + url;
            }

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                    );

            try {

                holder.itemView
                        .getContext()
                        .startActivity(intent);

            } catch (ActivityNotFoundException e) {

                Toast.makeText(
                        holder.itemView.getContext(),
                        "No app found to open this link.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // DELETE LINK
        holder.btnDeleteLink.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return;
            }

            if (deleteListener != null) {

                deleteListener.onDelete(
                        linkList.get(currentPosition),
                        currentPosition
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }
}