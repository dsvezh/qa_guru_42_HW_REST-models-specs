package api;

import io.qameta.allure.Step;
import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubReviewBodyModel;
import models.clubs.ClubReviewModel;
import models.clubs.ClubReviewValidationErrorResponseModel;
import models.clubs.ClubReviewsListResponseModel;
import models.clubs.ClubValidationErrorResponseModel;
import models.clubs.ClubWithoutBookTitleBodyModel;
import models.clubs.ClubsListResponseModel;
import models.login.WrongCredentialsLoginResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.clubs.ClubsSpec.authorizedClubsRequestSpec;
import static specs.clubs.ClubsSpec.clubReviewsInvalidPageResponseSpec;
import static specs.clubs.ClubsSpec.clubsRequestSpec;
import static specs.clubs.ClubsSpec.createClubReviewUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.createClubReviewValidationErrorResponseSpec;
import static specs.clubs.ClubsSpec.createClubUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.createClubValidationErrorResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubReviewNotFoundResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubReviewForbiddenResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubReviewUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubNotFoundResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.successfulCreateClubReviewResponseSpec;
import static specs.clubs.ClubsSpec.successfulCreateClubResponseSpec;
import static specs.clubs.ClubsSpec.successfulClubsListResponseSpec;
import static specs.clubs.ClubsSpec.successfulClubReviewsListResponseSpec;
import static specs.clubs.ClubsSpec.successfulDeleteClubReviewResponseSpec;
import static specs.clubs.ClubsSpec.successfulDeleteClubResponseSpec;
import static specs.clubs.ClubsSpec.successfulUpdateClubReviewResponseSpec;
import static specs.clubs.ClubsSpec.successfulUpdateClubResponseSpec;
import static specs.clubs.ClubsSpec.updateClubReviewUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.updateClubReviewForbiddenResponseSpec;
import static specs.clubs.ClubsSpec.updateClubReviewValidationErrorResponseSpec;
import static specs.clubs.ClubsSpec.updateClubUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.updateClubValidationErrorResponseSpec;

public class ClubsApiClient {

    @Step("Получение списка клубов GET /clubs/")
    public ClubsListResponseModel getClubs() {
        return given(clubsRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubsListResponseSpec)
                .extract()
                .as(ClubsListResponseModel.class);
    }

    @Step("Получение списка отзывов GET /clubs/reviews/")
    public ClubReviewsListResponseModel getClubReviews() {
        return given(clubsRequestSpec)
                .when()
                .get("/clubs/reviews/")
                .then()
                .spec(successfulClubReviewsListResponseSpec)
                .extract()
                .as(ClubReviewsListResponseModel.class);
    }

    @Step("Получение списка отзывов с некорректной страницей GET /clubs/reviews/")
    public WrongCredentialsLoginResponseModel getClubReviewsWithInvalidPage(String page) {
        return given(clubsRequestSpec)
                .queryParam("page", page)
                .when()
                .get("/clubs/reviews/")
                .then()
                .spec(clubReviewsInvalidPageResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Создание отзыва о клубе POST /clubs/reviews/")
    public ClubReviewModel createClubReview(String accessToken, ClubReviewBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(successfulCreateClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Создание отзыва о клубе без токена POST /clubs/reviews/")
    public WrongCredentialsLoginResponseModel createClubReviewWithoutToken(
            ClubReviewBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(createClubReviewUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Создание отзыва о клубе с ошибкой валидации POST /clubs/reviews/")
    public ClubReviewValidationErrorResponseModel createClubReviewWithValidationError(
            String accessToken, ClubReviewBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(createClubReviewValidationErrorResponseSpec)
                .extract()
                .as(ClubReviewValidationErrorResponseModel.class);
    }

    @Step("Обновление отзыва о клубе PUT /clubs/reviews/{id}/")
    public ClubReviewModel updateClubReview(
            String accessToken, Integer reviewId, ClubReviewBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(successfulUpdateClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Обновление отзыва о клубе без токена PUT /clubs/reviews/{id}/")
    public WrongCredentialsLoginResponseModel updateClubReviewWithoutToken(
            Integer reviewId, ClubReviewBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(updateClubReviewUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Обновление чужого отзыва о клубе PUT /clubs/reviews/{id}/")
    public WrongCredentialsLoginResponseModel updateAnotherUsersClubReview(
            String accessToken, Integer reviewId, ClubReviewBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(updateClubReviewForbiddenResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Обновление отзыва о клубе с ошибкой валидации PUT /clubs/reviews/{id}/")
    public ClubReviewValidationErrorResponseModel updateClubReviewWithValidationError(
            String accessToken, Integer reviewId, ClubReviewBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(updateClubReviewValidationErrorResponseSpec)
                .extract()
                .as(ClubReviewValidationErrorResponseModel.class);
    }

    @Step("Удаление отзыва о клубе DELETE /clubs/reviews/{id}/")
    public void deleteClubReview(String accessToken, Integer reviewId) {
        given(authorizedClubsRequestSpec(accessToken))
                .when()
                .delete("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(successfulDeleteClubReviewResponseSpec);
    }

    @Step("Удаление отзыва о клубе без токена DELETE /clubs/reviews/{id}/")
    public WrongCredentialsLoginResponseModel deleteClubReviewWithoutToken(Integer reviewId) {
        return given(baseRequestSpec)
                .when()
                .delete("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(deleteClubReviewUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Удаление чужого отзыва о клубе DELETE /clubs/reviews/{id}/")
    public WrongCredentialsLoginResponseModel deleteAnotherUsersClubReview(
            String accessToken, Integer reviewId) {
        return given(authorizedClubsRequestSpec(accessToken))
                .when()
                .delete("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(deleteClubReviewForbiddenResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Удаление несуществующего отзыва о клубе DELETE /clubs/reviews/{id}/")
    public WrongCredentialsLoginResponseModel deleteNonExistentClubReview(
            String accessToken, Integer reviewId) {
        return given(authorizedClubsRequestSpec(accessToken))
                .when()
                .delete("/clubs/reviews/{id}/", reviewId)
                .then()
                .spec(deleteClubReviewNotFoundResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Создание клуба POST /clubs/")
    public ClubModel createClub(String accessToken, ClubBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("Создание клуба без токена POST /clubs/")
    public WrongCredentialsLoginResponseModel createClubWithoutToken(ClubBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(createClubUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Создание клуба с ошибкой валидации POST /clubs/")
    public ClubValidationErrorResponseModel createClubWithValidationError(
            String accessToken, ClubWithoutBookTitleBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(createClubValidationErrorResponseSpec)
                .extract()
                .as(ClubValidationErrorResponseModel.class);
    }

    @Step("Обновление клуба PUT /clubs/{id}/")
    public ClubModel updateClub(String accessToken, Integer clubId, ClubBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/clubs/{id}/", clubId)
                .then()
                .spec(successfulUpdateClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("Обновление клуба без токена PUT /clubs/{id}/")
    public WrongCredentialsLoginResponseModel updateClubWithoutToken(
            Integer clubId, ClubBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .put("/clubs/{id}/", clubId)
                .then()
                .spec(updateClubUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Обновление клуба с ошибкой валидации PUT /clubs/{id}/")
    public ClubValidationErrorResponseModel updateClubWithValidationError(
            String accessToken, Integer clubId, ClubWithoutBookTitleBodyModel body) {
        return given(authorizedClubsRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/clubs/{id}/", clubId)
                .then()
                .spec(updateClubValidationErrorResponseSpec)
                .extract()
                .as(ClubValidationErrorResponseModel.class);
    }

    @Step("Удаление клуба DELETE /clubs/{clubId}/")
    public void deleteClub(String accessToken, Integer clubId) {
        given(authorizedClubsRequestSpec(accessToken))
                .when()
                .delete("/clubs/{id}/", clubId)
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Удаление клуба без токена DELETE /clubs/{clubId}/")
    public WrongCredentialsLoginResponseModel deleteClubWithoutToken(Integer clubId) {
        return given(baseRequestSpec)
                .when()
                .delete("/clubs/{id}/", clubId)
                .then()
                .spec(deleteClubUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Удаление несуществующего клуба DELETE /clubs/{clubId}/")
    public WrongCredentialsLoginResponseModel deleteNonExistentClub(
            String accessToken, Integer clubId) {
        return given(authorizedClubsRequestSpec(accessToken))
                .when()
                .delete("/clubs/{id}/", clubId)
                .then()
                .spec(deleteClubNotFoundResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }
}
