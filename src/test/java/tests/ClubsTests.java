package tests;

import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubValidationErrorResponseModel;
import models.clubs.ClubWithoutBookTitleBodyModel;
import models.clubs.ClubsListResponseModel;
import models.login.LoginBodyModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.DELETE_CLUB_NOT_FOUND_ERROR;
import static tests.TestData.REQUIRED_FIELD_ERROR;
import static tests.TestData.UNAUTHORIZED_ERROR;

public class ClubsTests extends TestBase {

    @Test
    public void getClubsReturns200AndValidStructure() {
        ClubsListResponseModel response = api.clubs.getClubs();

        step("Проверить структуру списка клубов", () -> {
            assertThat(response).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results())
                    .as("на одной странице не может быть больше клубов, чем count")
                    .hasSizeLessThanOrEqualTo(response.count());
            if (response.count() > 0) {
                assertThat(response.results()).isNotEmpty();
            }
        });
    }

    @Test
    public void getClubsPaginationWhenTotalExceedsPageSize() {
        ClubsListResponseModel response = api.clubs.getClubs();

        step("Проверить пагинацию списка клубов", () -> {
            assertThat(response.results())
                    .as("размер results не может превышать общий count")
                    .hasSizeLessThanOrEqualTo(response.count());

            if (response.count() > response.results().size()) {
                assertThat(response.next())
                        .as("если клубов больше, чем на странице, должна быть ссылка next")
                        .isNotNull();
            }
        });
    }

    @Test
    public void getClubsEachClubHasRequiredFields() {
        ClubsListResponseModel response = api.clubs.getClubs();

        step("Проверить обязательные поля каждого клуба", () -> {
            for (ClubModel club : response.results()) {
                assertThat(club.id()).isNotNull().isPositive();
                assertThat(club.bookTitle()).isNotNull();
                assertThat(club.bookAuthors()).isNotNull();
                assertThat(club.publicationYear()).isNotNull();
                assertThat(club.description()).isNotNull();
                assertThat(club.telegramChatLink()).isNotNull();
                assertThat(club.owner()).isNotNull().isPositive();
                assertThat(club.members()).isNotNull();
                assertThat(club.reviews()).isNotNull();
                assertThat(club.created()).isNotNull();
            }
        });
    }

    @Test
    public void getClubsPaginationFieldsPresent() {
        ClubsListResponseModel response = api.clubs.getClubs();

        step("Проверить поля пагинации", () -> {
            assertThat(response.count()).isNotNull();
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSizeLessThanOrEqualTo(response.count());
        });
    }

    @Test
    public void successfulCreateClubTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Test book " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created by API test",
                "https://t.me/test_book_club");

        ClubModel response = api.clubs.createClub(accessToken, clubData);

        step("Проверить данные созданного клуба", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.bookTitle()).isEqualTo(clubData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(clubData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(clubData.publicationYear());
            assertThat(response.description()).isEqualTo(clubData.description());
            assertThat(response.telegramChatLink()).isEqualTo(clubData.telegramChatLink());
            assertThat(response.owner()).isPositive();
            assertThat(response.members()).contains(response.owner());
            assertThat(response.reviews()).isEmpty();
            assertThat(response.created()).isNotBlank();
        });
    }

    @Test
    public void createClubWithBoundaryValuesTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        String uniqueSuffix = String.valueOf(System.nanoTime());
        ClubBodyModel clubData = new ClubBodyModel(
                "T".repeat(255 - uniqueSuffix.length()) + uniqueSuffix,
                "A".repeat(255),
                Integer.MAX_VALUE,
                "Boundary values test",
                "https://t.me/boundary_values_club");

        ClubModel response = api.clubs.createClub(accessToken, clubData);

        step("Проверить граничные значения созданного клуба", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.bookTitle()).hasSize(255).isEqualTo(clubData.bookTitle());
            assertThat(response.bookAuthors()).hasSize(255).isEqualTo(clubData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(Integer.MAX_VALUE);
            assertThat(response.owner()).isPositive();
        });
    }

    @Test
    public void createClubWithoutTokenTest() {
        ClubBodyModel clubData = new ClubBodyModel(
                "Unauthorized club",
                "Test author",
                2024,
                "Club must not be created",
                "https://t.me/unauthorized_club");

        WrongCredentialsLoginResponseModel response =
                api.clubs.createClubWithoutToken(clubData);

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @Test
    public void createClubWithoutBookTitleTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubWithoutBookTitleBodyModel clubData = new ClubWithoutBookTitleBodyModel(
                "Test author",
                2024,
                "Club without required book title",
                "https://t.me/club_without_title");

        ClubValidationErrorResponseModel response =
                api.clubs.createClubWithValidationError(accessToken, clubData);

        step("Проверить ошибку обязательного поля bookTitle",
                () -> assertThat(response.bookTitle())
                        .containsExactly(REQUIRED_FIELD_ERROR));
    }

    @Test
    public void successfulUpdateClubTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel initialClubData = new ClubBodyModel(
                "Initial book " + System.currentTimeMillis(),
                "Initial author",
                2023,
                "Initial description",
                "https://t.me/initial_book_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, initialClubData);

        ClubBodyModel updateData = new ClubBodyModel(
                "Updated book " + System.currentTimeMillis(),
                "Updated author",
                2024,
                "Updated book club description",
                "https://t.me/updated_book_club");
        ClubModel response =
                api.clubs.updateClub(accessToken, createdClub.id(), updateData);

        step("Проверить обновлённые данные клуба", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.bookTitle()).isEqualTo(updateData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(updateData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(updateData.publicationYear());
            assertThat(response.description()).isEqualTo(updateData.description());
            assertThat(response.telegramChatLink()).isEqualTo(updateData.telegramChatLink());
            assertThat(response.owner()).isEqualTo(createdClub.owner());
            assertThat(response.modified()).isNotBlank();
        });
    }

    @Test
    public void updateClubWithBoundaryValuesTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel initialClubData = new ClubBodyModel(
                "Initial book " + System.currentTimeMillis(),
                "Initial author",
                2023,
                "Initial description",
                "https://t.me/initial_boundary_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, initialClubData);

        String uniqueSuffix = String.valueOf(System.nanoTime());
        ClubBodyModel updateData = new ClubBodyModel(
                "T".repeat(255 - uniqueSuffix.length()) + uniqueSuffix,
                "A".repeat(255),
                Integer.MAX_VALUE,
                "Boundary values update test",
                "https://t.me/updated_boundary_club");
        ClubModel response =
                api.clubs.updateClub(accessToken, createdClub.id(), updateData);

        step("Проверить граничные значения обновлённого клуба", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.bookTitle()).hasSize(255).isEqualTo(updateData.bookTitle());
            assertThat(response.bookAuthors()).hasSize(255).isEqualTo(updateData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(Integer.MAX_VALUE);
            assertThat(response.modified()).isNotBlank();
        });
    }

    @Test
    public void updateClubWithoutTokenTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel initialClubData = new ClubBodyModel(
                "Initial book " + System.currentTimeMillis(),
                "Initial author",
                2023,
                "Initial description",
                "https://t.me/unauthorized_update_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, initialClubData);

        ClubBodyModel updateData = new ClubBodyModel(
                "Unauthorized update",
                "Updated author",
                2024,
                "Club must not be updated",
                "https://t.me/unauthorized_updated_club");
        WrongCredentialsLoginResponseModel response =
                api.clubs.updateClubWithoutToken(createdClub.id(), updateData);

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @Test
    public void updateClubWithoutBookTitleTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel initialClubData = new ClubBodyModel(
                "Initial book " + System.currentTimeMillis(),
                "Initial author",
                2023,
                "Initial description",
                "https://t.me/validation_update_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, initialClubData);

        ClubWithoutBookTitleBodyModel updateData = new ClubWithoutBookTitleBodyModel(
                "Updated author",
                2024,
                "Club update without required book title",
                "https://t.me/updated_club_without_title");
        ClubValidationErrorResponseModel response =
                api.clubs.updateClubWithValidationError(accessToken, createdClub.id(), updateData);

        step("Проверить ошибку обязательного поля bookTitle",
                () -> assertThat(response.bookTitle())
                        .containsExactly(REQUIRED_FIELD_ERROR));
    }

    @Test
    public void successfulDeleteClubTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Book to delete " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Book club created for deletion",
                "https://t.me/book_club_to_delete");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void deleteClubWithBoundaryValuesTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        String uniqueSuffix = String.valueOf(System.nanoTime());
        ClubBodyModel clubData = new ClubBodyModel(
                "T".repeat(255 - uniqueSuffix.length()) + uniqueSuffix,
                "A".repeat(255),
                Integer.MAX_VALUE,
                "Boundary values delete test",
                "https://t.me/boundary_values_delete_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        api.clubs.deleteClub(accessToken, createdClub.id());
    }

    @Test
    public void deleteClubWithoutTokenTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        ClubBodyModel clubData = new ClubBodyModel(
                "Unauthorized delete " + System.currentTimeMillis(),
                "Test author",
                2024,
                "Club must not be deleted",
                "https://t.me/unauthorized_delete_club");
        ClubModel createdClub = api.clubs.createClub(accessToken, clubData);

        WrongCredentialsLoginResponseModel response =
                api.clubs.deleteClubWithoutToken(createdClub.id());

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @Test
    public void deleteNonExistentClubTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String accessToken =
                api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));

        WrongCredentialsLoginResponseModel response =
                api.clubs.deleteNonExistentClub(accessToken, Integer.MAX_VALUE);

        step("Проверить ошибку отсутствия книжного клуба",
                () -> assertThat(response.detail()).isEqualTo(DELETE_CLUB_NOT_FOUND_ERROR));
    }
}
