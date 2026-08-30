package api;

import io.qameta.allure.Step;
import models.login.WrongCredentialsLoginResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.updateuser.UpdateUserBodyModel;
import models.updateuser.UpdateUserFirstNameBodyModel;
import models.updateuser.UpdateUserInvalidTokenResponseModel;
import models.updateuser.UpdateUserPutUsernameOnlyBodyModel;
import models.updateuser.UpdateUserValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.registrationValidationErrorResponseSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.updateuser.UpdateUserSpec.successfulUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.updateUserInvalidTokenResponseSpec;
import static specs.updateuser.UpdateUserSpec.updateUserRequestSpec;
import static specs.updateuser.UpdateUserSpec.updateUserUnauthorizedResponseSpec;
import static specs.updateuser.UpdateUserSpec.updateUserValidationErrorResponseSpec;
import static tests.TestData.UPDATE_USER_INVALID_ACCESS_TOKEN;

public class UsersApiClient {

    @Step("Регистрация пользователя POST /users/register/")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Регистрация существующего пользователя POST /users/register/")
    public ExistingUserResponseModel registerExistingUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class);
    }

    @Step("Регистрация с ошибкой валидации POST /users/register/")
    public RegistrationValidationErrorResponseModel registerWithValidationError(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(registrationValidationErrorResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }

    @Step("Обновление профиля пользователя PATCH /users/me/")
    public SuccessfulRegistrationResponseModel updateUser(String accessToken, UpdateUserBodyModel body) {
        return given(updateUserRequestSpec(accessToken))
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Частичное обновление профиля PATCH /users/me/")
    public SuccessfulRegistrationResponseModel updateUserPartial(
            String accessToken, UpdateUserFirstNameBodyModel body) {
        return given(updateUserRequestSpec(accessToken))
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Обновление профиля без токена PATCH /users/me/")
    public WrongCredentialsLoginResponseModel updateUserWithoutToken(UpdateUserBodyModel body) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(updateUserUnauthorizedResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Обновление профиля с невалидным токеном PATCH /users/me/")
    public UpdateUserInvalidTokenResponseModel updateUserWithInvalidToken(UpdateUserBodyModel body) {
        return given(updateUserRequestSpec(UPDATE_USER_INVALID_ACCESS_TOKEN))
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(updateUserInvalidTokenResponseSpec)
                .extract()
                .as(UpdateUserInvalidTokenResponseModel.class);
    }

    @Step("Замена профиля с ошибкой валидации PUT /users/me/")
    public UpdateUserValidationErrorResponseModel replaceUserWithValidationError(
            String accessToken, UpdateUserPutUsernameOnlyBodyModel body) {
        return given(updateUserRequestSpec(accessToken))
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(updateUserValidationErrorResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }
}
