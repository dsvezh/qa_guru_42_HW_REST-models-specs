package tests.ui;

import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.ClubDetailsPage;
import pages.ClubFormPage;
import pages.ClubsPage;
import pages.LoginPage;
import tests.TestBase;
import tests.fixtures.ApiFixture;
import tests.fixtures.ApiFixture.TestUser;
import tests.fixtures.UiTestDataFactory;

@Tag("ui")
public class ClubsUiTests extends TestBase {

    private ApiFixture fixture;

    @BeforeEach
    void prepareFixture() {
        fixture = new ApiFixture(api);
    }

    @AfterEach
    void cleanUp() {
        fixture.close();
    }

    @Test
    @DisplayName("Каталог отображает клуб, созданный через API")
    void catalogShowsApiCreatedClub() {
        TestUser owner = fixture.createUser("catalog");
        ClubBodyModel clubData = UiTestDataFactory.club("catalog");
        fixture.createClub(owner, clubData);

        new ClubsPage()
                .openPage()
                .search(clubData.bookTitle())
                .shouldContainClub(
                        clubData.bookTitle(),
                        clubData.bookAuthors(),
                        clubData.publicationYear(),
                        clubData.description());
    }

    @Test
    @DisplayName("Страница клуба отображает отзыв, созданный через API")
    void clubPageShowsApiCreatedReview() {
        TestUser owner = fixture.createUser("review_view");
        ClubBodyModel clubData = UiTestDataFactory.club("review view");
        ClubModel club = fixture.createClub(owner, clubData);
        String reviewText = "Отзыв подготовлен через API " + club.id();
        fixture.createReview(owner, club.id(), reviewText, 5, 321);

        new ClubDetailsPage()
                .openPage(club.id())
                .shouldHaveClub(
                        clubData.bookTitle(),
                        clubData.bookAuthors(),
                        clubData.publicationYear(),
                        clubData.description())
                .shouldHaveReview(owner.username(), reviewText, 5, 321);
    }

    @Test
    @DisplayName("Участник создаёт через UI отзыв в клубе, подготовленном через API")
    void memberCreatesReviewThroughUi() {
        TestUser owner = fixture.createUser("review_create");
        ClubBodyModel clubData = UiTestDataFactory.club("review create");
        ClubModel club = fixture.createClub(owner, clubData);
        String reviewText = "UI-отзыв для клуба " + club.id();

        new LoginPage()
                .openPage()
                .login(owner.username(), owner.password());

        new ClubDetailsPage()
                .openPage(club.id())
                .createReview(reviewText, 4, 256)
                .shouldHaveReview(owner.username(), reviewText, 4, 256);
    }

    @Test
    @DisplayName("У автора есть кнопка «Редактировать» на своём отзыве")
    void authorSeesEditButtonOnOwnReview() {
        TestUser owner = fixture.createUser("review_edit");
        ClubBodyModel clubData = UiTestDataFactory.club("review edit");
        ClubModel club = fixture.createClub(owner, clubData);
        String reviewText = "Свой отзыв для клуба " + club.id();
        fixture.createReview(owner, club.id(), reviewText, 5, 210);

        new LoginPage()
                .openPage()
                .login(owner.username(), owner.password());

        new ClubDetailsPage()
                .openPage(club.id())
                .shouldHaveReview(owner.username(), reviewText, 5, 210)
                .shouldHaveEditReviewButton(reviewText);
    }

    @Test
    @DisplayName("У пользователя нет кнопки «Редактировать» на чужом отзыве")
    void userDoesNotSeeEditButtonOnAnotherUsersReview() {
        TestUser owner = fixture.createUser("review_owner");
        TestUser anotherUser = fixture.createUser("review_viewer");
        ClubBodyModel clubData = UiTestDataFactory.club("review foreign");
        ClubModel club = fixture.createClub(owner, clubData);
        String reviewText = "Чужой отзыв для клуба " + club.id();
        fixture.createReview(owner, club.id(), reviewText, 4, 180);

        new LoginPage()
                .openPage()
                .login(anotherUser.username(), anotherUser.password());

        new ClubDetailsPage()
                .openPage(club.id())
                .shouldHaveReview(owner.username(), reviewText, 4, 180)
                .shouldNotHaveEditReviewButton(reviewText);
    }
}
