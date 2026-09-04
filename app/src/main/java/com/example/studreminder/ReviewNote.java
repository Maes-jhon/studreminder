package com.example.studreminder;

public class ReviewNote {

    private int id;
    private int reviewId;
    private String title;
    private String content;

    public ReviewNote(
            int id,
            int reviewId,
            String title,
            String content
    ) {

        this.id = id;
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}