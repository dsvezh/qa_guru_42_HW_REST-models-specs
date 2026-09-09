package models.clubs;

import java.util.List;

/**
 * Ответ GET /clubs/reviews/ — пагинированный список отзывов.
 */
public record ClubReviewsListResponseModel(
        Integer count,
        String next,
        String previous,
        List<ClubReviewModel> results
) {}
