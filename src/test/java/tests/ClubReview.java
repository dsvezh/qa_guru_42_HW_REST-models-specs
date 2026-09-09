package tests;

import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubReviewBodyModel;
import models.clubs.ClubReviewModel;
import models.clubs.ClubReviewValidationErrorResponseModel;
import models.clubs.ClubReviewsListResponseModel;
import models.login.LoginBodyModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.CLUB_REVIEWS_INVALID_PAGE_ERROR;
import static tests.TestData.DELETE_CLUB_REVIEW_NOT_FOUND_ERROR;
import static tests.TestData.FORBIDDEN_ERROR;
import static tests.TestData.MAX_ASSESSMENT_ERROR;
import static tests.TestData.UNAUTHORIZED_ERROR;

public class ClubReview extends TestBase {

    @Test
    public void getClubReviewsReturns200AndValidStructure() {
        ClubReviewsListResponseModel response = api.clubs.getClubReviews();

        step("Проверить структуру списка отзывов", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results())
                    .as("на одной странице не может быть больше отзывов, чем count")
                    .hasSizeLessThanOrEqualTo(response.count());
            if (response.count() > 0) {
                assertThat(response.results()).isNotEmpty();
            }
        });
    }

    @Test
    public void getClubReviewsEachReviewHasRequiredFields() {
        ClubReviewsListResponseModel response = api.clubs.getClubReviews();

        step("Проверить обязательные поля каждого отзыва", () -> {
            for (ClubReviewModel review : response.results()) {
                assertThat(review.id()).isNotNull().isPositive();
                assertThat(review.club()).isNotNull().isPositive();
                assertThat(review.user()).isNotNull();
                assertThat(review.user().id()).isNotNull().isPositive();
                assertThat(review.user().username()).isNotBlank();
                assertThat(review.review()).isNotNull();
                assertThat(review.assessment()).isBetween(1, 5);
                assertThat(review.readPages()).isNotNull().isGreaterThanOrEqualTo(0);
                assertThat(review.created()).isNotBlank();
            }
        });
    }

    @Test
    public void getClubReviewsWithZeroPageTest() {
        WrongCredentialsLoginResponseModel response =
                api.clubs.getClubReviewsWithInvalidPage("0");

        step("Проверить ошибку нулевого номера страницы",
                () -> assertThat(response.detail()).isEqualTo(CLUB_REVIEWS_INVALID_PAGE_ERROR));
    }

    @Test
    public void getClubReviewsWithNonNumericPageTest() {
        WrongCredentialsLoginResponseModel response =
                api.clubs.getClubReviewsWithInvalidPage("abc");

        step("Проверить ошибку нечислового номера страницы",
                () -> assertThat(response.detail()).isEqualTo(CLUB_REVIEWS_INVALID_PAGE_ERROR));
    }

    @Test
    public void successfulCreateClubReviewTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for review " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for review",
                "https://t.me/book_club_for_review");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(createdClub.id(), "Excellent book", 5, 300);
        ClubReviewModel response = api.clubs.createClubReview(accessToken, reviewData);

        step("Проверить данные созданного отзыва", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.club()).isEqualTo(createdClub.id());
            assertThat(response.user()).isNotNull();
            assertThat(response.user().id()).isPositive();
            assertThat(response.user().username()).isEqualTo(username);
            assertThat(response.review()).isEqualTo(reviewData.review());
            assertThat(response.assessment()).isEqualTo(reviewData.assessment());
            assertThat(response.readPages()).isEqualTo(reviewData.readPages());
            assertThat(response.created()).isNotBlank();
        });

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void createClubReviewWithMinimumBoundaryValuesTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for boundary review " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for boundary review",
                "https://t.me/book_club_for_boundary_review");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(createdClub.id(), "A", 1, 0);
        ClubReviewModel response = api.clubs.createClubReview(accessToken, reviewData);

        step("Проверить минимальные граничные значения созданного отзыва", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.club()).isEqualTo(createdClub.id());
            assertThat(response.review()).isEqualTo("A");
            assertThat(response.assessment()).isEqualTo(1);
            assertThat(response.readPages()).isZero();
            assertThat(response.created()).isNotBlank();
        });

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void createClubReviewWithoutTokenTest() {
        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(Integer.MAX_VALUE, "Unauthorized review", 5, 100);

        WrongCredentialsLoginResponseModel response =
                api.clubs.createClubReviewWithoutToken(reviewData);

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail())
                        .isEqualTo(UNAUTHORIZED_ERROR));
    }

    @Test
    public void createClubReviewWithAssessmentAboveMaximumTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for invalid review " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for invalid review",
                "https://t.me/book_club_for_invalid_review");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(createdClub.id(), "Invalid assessment", 6, 100);
        ClubReviewValidationErrorResponseModel response =
                api.clubs.createClubReviewWithValidationError(accessToken, reviewData);

        step("Проверить ошибку максимального значения assessment",
                () -> assertThat(response.assessment())
                        .containsExactly(MAX_ASSESSMENT_ERROR));

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void successfulUpdateClubReviewTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for review update " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for review update",
                "https://t.me/book_club_for_review_update");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel initialReviewData =
                new ClubReviewBodyModel(createdClub.id(), "Initial review", 3, 100);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, initialReviewData);

        ClubReviewBodyModel updateData =
                new ClubReviewBodyModel(createdClub.id(), "Updated review", 5, 300);
        ClubReviewModel response =
                api.clubs.updateClubReview(accessToken, createdReview.id(), updateData);

        step("Проверить обновлённые данные отзыва", () -> {
            assertThat(response.id()).isEqualTo(createdReview.id());
            assertThat(response.club()).isEqualTo(createdClub.id());
            assertThat(response.user()).isNotNull();
            assertThat(response.user().id()).isEqualTo(createdReview.user().id());
            assertThat(response.user().username()).isEqualTo(username);
            assertThat(response.review()).isEqualTo(updateData.review());
            assertThat(response.assessment()).isEqualTo(updateData.assessment());
            assertThat(response.readPages()).isEqualTo(updateData.readPages());
            assertThat(response.created()).isEqualTo(createdReview.created());
            assertThat(response.modified()).isNotBlank();
        });

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void updateClubReviewWithMinimumBoundaryValuesTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for boundary review update " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for boundary review update",
                "https://t.me/book_club_for_boundary_review_update");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel initialReviewData =
                new ClubReviewBodyModel(createdClub.id(), "Initial review", 5, 300);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, initialReviewData);

        ClubReviewBodyModel updateData =
                new ClubReviewBodyModel(createdClub.id(), "A", 1, 0);
        ClubReviewModel response =
                api.clubs.updateClubReview(accessToken, createdReview.id(), updateData);

        step("Проверить минимальные граничные значения обновлённого отзыва", () -> {
            assertThat(response.id()).isEqualTo(createdReview.id());
            assertThat(response.review()).isEqualTo("A");
            assertThat(response.assessment()).isEqualTo(1);
            assertThat(response.readPages()).isZero();
            assertThat(response.modified()).isNotBlank();
        });

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void updateClubReviewWithoutTokenTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for unauthorized review update " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for unauthorized review update",
                "https://t.me/book_club_for_unauthorized_review_update");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel initialReviewData =
                new ClubReviewBodyModel(createdClub.id(), "Initial review", 3, 100);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, initialReviewData);

        ClubReviewBodyModel updateData =
                new ClubReviewBodyModel(createdClub.id(), "Unauthorized update", 5, 300);
        WrongCredentialsLoginResponseModel response =
                api.clubs.updateClubReviewWithoutToken(createdReview.id(), updateData);

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail())
                        .isEqualTo(UNAUTHORIZED_ERROR));

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void updateAnotherUsersClubReviewForbiddenTest() {
        String uniqueSuffix = String.valueOf(System.nanoTime());
        String ownerUsername = "review_owner_" + uniqueSuffix;
        String password = "pass_" + uniqueSuffix;
        api.users.register(new RegistrationBodyModel(ownerUsername, password));
        String ownerToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(ownerUsername, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for review permissions " + uniqueSuffix,
                "Test author",
                2024,
                "Book club created for review permissions",
                "https://t.me/review_permissions_" + uniqueSuffix);
        ClubModel createdClub = api.clubs.createClub(ownerToken, clubData);
        ClubReviewModel createdReview = api.clubs.createClubReview(
                ownerToken,
                new ClubReviewBodyModel(createdClub.id(), "Owner review", 4, 200));

        String anotherUsername = "another_user_" + uniqueSuffix;
        api.users.register(new RegistrationBodyModel(anotherUsername, password));
        String anotherUserToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(anotherUsername, password));

        ClubReviewBodyModel updateData =
                new ClubReviewBodyModel(createdClub.id(), "Another user's update", 1, 0);
        WrongCredentialsLoginResponseModel response =
                api.clubs.updateAnotherUsersClubReview(
                        anotherUserToken, createdReview.id(), updateData);

        step("Проверить запрет изменения чужого отзыва",
                () -> assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));

        api.clubs.deleteClub(ownerToken, createdClub.id());
    }

    @Test
    public void updateClubReviewWithAssessmentAboveMaximumTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for invalid review update " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for invalid review update",
                "https://t.me/book_club_for_invalid_review_update");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel initialReviewData =
                new ClubReviewBodyModel(createdClub.id(), "Initial review", 3, 100);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, initialReviewData);

        ClubReviewBodyModel updateData =
                new ClubReviewBodyModel(createdClub.id(), "Invalid assessment", 6, 100);
        ClubReviewValidationErrorResponseModel response =
                api.clubs.updateClubReviewWithValidationError(
                        accessToken, createdReview.id(), updateData);

        step("Проверить ошибку максимального значения assessment",
                () -> assertThat(response.assessment())
                        .containsExactly(MAX_ASSESSMENT_ERROR));

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void successfulDeleteClubReviewTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for review deletion " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for review deletion",
                "https://t.me/book_club_for_review_deletion");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(createdClub.id(), "Review to delete", 5, 300);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, reviewData);

        api.clubs.deleteClubReview(accessToken, createdReview.id());
        
        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void deleteClubReviewWithoutTokenTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for unauthorized review deletion " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for unauthorized review deletion",
                "https://t.me/book_club_for_unauthorized_review_deletion");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        ClubReviewBodyModel reviewData =
                new ClubReviewBodyModel(createdClub.id(), "Review must not be deleted", 3, 100);
        ClubReviewModel createdReview =
                api.clubs.createClubReview(accessToken, reviewData);

        WrongCredentialsLoginResponseModel response =
                api.clubs.deleteClubReviewWithoutToken(createdReview.id());

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail())
                        .isEqualTo(UNAUTHORIZED_ERROR));

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void deleteAnotherUsersClubReviewForbiddenTest() {
        String uniqueSuffix = String.valueOf(System.nanoTime());
        String ownerUsername = "review_owner_" + uniqueSuffix;
        String password = "pass_" + uniqueSuffix;
        api.users.register(new RegistrationBodyModel(ownerUsername, password));
        String ownerToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(ownerUsername, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book for review deletion permissions " + uniqueSuffix,
                "Test author",
                2024,
                "Book club created for review deletion permissions",
                "https://t.me/review_deletion_permissions_" + uniqueSuffix);
        ClubModel createdClub = api.clubs.createClub(ownerToken, clubData);
        ClubReviewModel createdReview = api.clubs.createClubReview(
                ownerToken,
                new ClubReviewBodyModel(createdClub.id(), "Owner review", 4, 200));

        String anotherUsername = "another_user_" + uniqueSuffix;
        api.users.register(new RegistrationBodyModel(anotherUsername, password));
        String anotherUserToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(anotherUsername, password));

        WrongCredentialsLoginResponseModel response =
                api.clubs.deleteAnotherUsersClubReview(anotherUserToken, createdReview.id());

        step("Проверить запрет удаления чужого отзыва",
                () -> assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));

        api.clubs.deleteClub(ownerToken, createdClub.id());
    }

    @Test
    public void deleteNonExistentClubReviewTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        WrongCredentialsLoginResponseModel response =
                api.clubs.deleteNonExistentClubReview(accessToken, Integer.MAX_VALUE);

        step("Проверить ошибку отсутствия отзыва о клубе",
                () -> assertThat(response.detail())
                        .isEqualTo(DELETE_CLUB_REVIEW_NOT_FOUND_ERROR));
    }
}
