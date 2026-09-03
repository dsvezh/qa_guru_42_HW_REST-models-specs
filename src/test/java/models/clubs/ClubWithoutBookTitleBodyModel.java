package models.clubs;

public record ClubWithoutBookTitleBodyModel(
        String bookAuthors,
        Integer publicationYear,
        String description,
        String telegramChatLink
) {}
