package tests;

import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsTests extends TestBase {

    @Test
    public void getClubsReturns200AndValidStructure() {
        ClubsListResponseModel response =
                step("Получить список книжных клубов", () -> api.clubs.getClubs());

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
        ClubsListResponseModel response =
                step("Получить список книжных клубов", () -> api.clubs.getClubs());

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
        ClubsListResponseModel response =
                step("Получить список книжных клубов", () -> api.clubs.getClubs());

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
        ClubsListResponseModel response =
                step("Получить список книжных клубов", () -> api.clubs.getClubs());

        step("Проверить поля пагинации", () -> {
            assertThat(response.count()).isNotNull();
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).hasSizeLessThanOrEqualTo(response.count());
        });
    }
}
