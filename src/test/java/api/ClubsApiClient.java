package api;

import io.qameta.allure.Step;
import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubValidationErrorResponseModel;
import models.clubs.ClubWithoutBookTitleBodyModel;
import models.clubs.ClubsListResponseModel;
import models.login.WrongCredentialsLoginResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.clubs.ClubsSpec.authorizedClubsRequestSpec;
import static specs.clubs.ClubsSpec.clubsRequestSpec;
import static specs.clubs.ClubsSpec.createClubUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.createClubValidationErrorResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubNotFoundResponseSpec;
import static specs.clubs.ClubsSpec.deleteClubUnauthorizedResponseSpec;
import static specs.clubs.ClubsSpec.successfulCreateClubResponseSpec;
import static specs.clubs.ClubsSpec.successfulClubsListResponseSpec;
import static specs.clubs.ClubsSpec.successfulDeleteClubResponseSpec;
import static specs.clubs.ClubsSpec.successfulUpdateClubResponseSpec;
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
