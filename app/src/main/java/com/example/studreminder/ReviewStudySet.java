package com.example.studreminder;

public class ReviewStudySet {

    private int id;
    private int reviewId;
    private String question;
    private String answer;

    public ReviewStudySet(
            int id,
            int reviewId,
            String question,
            String answer
    ) {

        this.id = id;
        this.reviewId = reviewId;
        this.question = question;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}