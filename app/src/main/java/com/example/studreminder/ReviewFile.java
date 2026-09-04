package com.example.studreminder;

public class ReviewFile {

    private int id;
    private int reviewId;
    private String fileName;
    private String fileUri;

    public ReviewFile(
            int id,
            int reviewId,
            String fileName,
            String fileUri
    ) {
        this.id = id;
        this.reviewId = reviewId;
        this.fileName = fileName;
        this.fileUri = fileUri;
    }

    public int getId() {
        return id;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUri() {
        return fileUri;
    }
}