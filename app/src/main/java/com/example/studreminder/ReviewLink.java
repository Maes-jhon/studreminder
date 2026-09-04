package com.example.studreminder;

public class ReviewLink {

    private int id;
    private int reviewId;
    private String title;
    private String url;

    public ReviewLink(
            int id,
            int reviewId,
            String title,
            String url
    ) {

        this.id = id;
        this.reviewId = reviewId;
        this.title = title;
        this.url = url;
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

    public String getUrl() {
        return url;
    }
}