package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class ClubDetailsPage {

    private final SelenideElement clubTitle = $("h1");
    private final SelenideElement clubAuthors = $(".authors");
    private final SelenideElement clubYear = $(".year");
    private final SelenideElement clubDescription = $(".description");

    private final SelenideElement writeReviewButton = $x("//button[normalize-space()='Написать отзыв']");
    private final SelenideElement assessmentInput = $("#assessment");
    private final SelenideElement readPagesInput = $("#readPages");
    private final SelenideElement reviewInput = $("#review");
    private final SelenideElement publishButton = $x("//button[normalize-space()='Опубликовать']");

    private final ElementsCollection reviewCards = $$(".review-card");
    private final String reviewerName = ".reviewer-name";
    private final String reviewContent = ".review-content";
    private final String reviewStars = ".stars";
    private final String reviewReadPages = ".read-pages";
    private final String editReviewButton = ".//button[normalize-space()='Редактировать']";

    @Step("Открыть страницу клуба с id={clubId}")
    public ClubDetailsPage openPage(Integer clubId) {
        open("/clubs/" + clubId);
        return this;
    }

    @Step("Проверить информацию о клубе «{bookTitle}»")
    public ClubDetailsPage shouldHaveClub(
            String bookTitle,
            String authors,
            Integer publicationYear,
            String description) {
        clubTitle.shouldHave(exactText(bookTitle));
        clubAuthors.shouldHave(text(authors));
        clubYear.shouldHave(exactText(publicationYear.toString()));
        clubDescription.shouldHave(exactText(description));
        return this;
    }

    @Step("Создать отзыв с оценкой {assessment}")
    public ClubDetailsPage createReview(String review, Integer assessment, Integer readPages) {
        writeReviewButton.shouldBe(visible).click();
        assessmentInput.setValue(assessment.toString());
        readPagesInput.setValue(readPages.toString());
        reviewInput.setValue(review);
        publishButton.click();
        return this;
    }

    @Step("Проверить отзыв пользователя {username}")
    public ClubDetailsPage shouldHaveReview(
            String username,
            String review,
            Integer assessment,
            Integer readPages) {
        SelenideElement card = reviewCard(review);
        card.$(reviewerName).shouldHave(exactText(username));
        card.$(reviewContent).shouldHave(text(review));
        card.$(reviewStars).shouldHave(exactText(stars(assessment)));
        card.$(reviewReadPages).shouldHave(exactText(readPages + " стр."));
        return this;
    }

    @Step("Проверить, что у отзыва есть кнопка «Редактировать»")
    public ClubDetailsPage shouldHaveEditReviewButton(String review) {
        reviewCard(review).$x(editReviewButton).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что у отзыва нет кнопки «Редактировать»")
    public ClubDetailsPage shouldNotHaveEditReviewButton(String review) {
        reviewCard(review).$x(editReviewButton).shouldNotBe(visible);
        return this;
    }

    private SelenideElement reviewCard(String review) {
        return reviewCards.findBy(text(review)).shouldBe(visible);
    }

    private String stars(Integer assessment) {
        return "★".repeat(assessment) + "☆".repeat(5 - assessment);
    }
}
