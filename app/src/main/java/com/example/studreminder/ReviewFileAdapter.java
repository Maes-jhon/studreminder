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

public class ReviewFileAdapter
        extends RecyclerView.Adapter<ReviewFileAdapter.ViewHolder> {

    private final List<ReviewFile> fileList;
    private final OnFileDeleteListener deleteListener;

    public interface OnFileDeleteListener {
        void onDelete(ReviewFile reviewFile, int position);
    }

    public ReviewFileAdapter(
            List<ReviewFile> fileList,
            OnFileDeleteListener deleteListener
    ) {
        this.fileList = fileList;
        this.deleteListener = deleteListener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvFileName;
        TextView tvFileType;
        ImageButton btnDeleteFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFileName =
                    itemView.findViewById(
                            R.id.tvFileName
                    );

            tvFileType =
                    itemView.findViewById(
                            R.id.tvFileType
                    );

            btnDeleteFile =
                    itemView.findViewById(
                            R.id.btnDeleteFile
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
                                R.layout.item_review_file,
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

        ReviewFile reviewFile =
                fileList.get(position);

        holder.tvFileName.setText(
                reviewFile.getFileName()
        );

        holder.tvFileType.setText(
                getFileType(
                        reviewFile.getFileName()
                )
        );


        // TAP FILE TO OPEN IT
        holder.itemView.setOnClickListener(v -> {

            Uri uri = Uri.parse(
                    reviewFile.getFileUri()
            );

            String mimeType =
                    holder.itemView
                            .getContext()
                            .getContentResolver()
                            .getType(uri);

            if (mimeType == null) {
                mimeType = "*/*";
            }

            Intent intent =
                    new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(
                    uri,
                    mimeType
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            try {

                holder.itemView
                        .getContext()
                        .startActivity(intent);

            } catch (ActivityNotFoundException e) {

                Toast.makeText(
                        holder.itemView.getContext(),
                        "No app found to open this file.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // DELETE FILE
        holder.btnDeleteFile.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition ==
                    RecyclerView.NO_POSITION) {
                return;
            }

            if (deleteListener != null) {

                deleteListener.onDelete(
                        fileList.get(currentPosition),
                        currentPosition
                );
            }
        });
    }

    private String getFileType(
            String fileName
    ) {

        String lowerName =
                fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) {

            return "PDF Document";

        } else if (
                lowerName.endsWith(".doc")
                        || lowerName.endsWith(".docx")
        ) {

            return "Word Document";

        } else if (
                lowerName.endsWith(".ppt")
                        || lowerName.endsWith(".pptx")
        ) {

            return "PowerPoint";

        } else if (
                lowerName.endsWith(".xls")
                        || lowerName.endsWith(".xlsx")
        ) {

            return "Excel Spreadsheet";

        } else if (
                lowerName.endsWith(".jpg")
                        || lowerName.endsWith(".jpeg")
                        || lowerName.endsWith(".png")
        ) {

            return "Image";

        } else {

            return "Attached File";
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}