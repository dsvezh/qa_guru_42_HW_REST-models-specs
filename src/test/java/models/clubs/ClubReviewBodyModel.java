package models.clubs;

public record ClubReviewBodyModel(
        Integer club,
        String review,
        Integer assessment,
        Integer readPages
) {}
