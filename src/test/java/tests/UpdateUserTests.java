package tests;

import models.login.LoginBodyModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.updateuser.UpdateUserBodyModel;
import models.updateuser.UpdateUserFirstNameBodyModel;
import models.updateuser.UpdateUserInvalidTokenResponseModel;
import models.updateuser.UpdateUserPutUsernameOnlyBodyModel;
import models.updateuser.UpdateUserValidationErrorResponseModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.UPDATE_USER_INVALID_TOKEN_CODE;
import static tests.TestData.UPDATE_USER_INVALID_TOKEN_ERROR;
import static tests.TestData.UPDATE_USER_REQUIRED_FIELD_ERROR;
import static tests.TestData.UPDATE_USER_UNAUTHORIZED_ERROR;

public class UpdateUserTests extends TestBase {

    @Test
    public void successfulUpdateUserTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        SuccessfulRegistrationResponseModel registeredUser =
                step("Зарегистрировать нового пользователя",
                        () -> api.users.register(new RegistrationBodyModel(username, password)));

        String accessToken = step("Авторизоваться и получить access токен",
                () -> api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password)));

        String firstName = "Ivan";
        String lastName = "Petrov";
        String email = "ivan.petrov@example.com";
        UpdateUserBodyModel updateData = new UpdateUserBodyModel(firstName, lastName, email);

        SuccessfulRegistrationResponseModel updateResponse =
                step("Обновить данные пользователя",
                        () -> api.users.updateUser(accessToken, updateData));

        step("Проверить обновлённые данные пользователя", () -> {
            assertThat(updateResponse.id()).isEqualTo(registeredUser.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);
        });
    }

    @Test
    public void successfulPartialUpdateUserTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        SuccessfulRegistrationResponseModel registeredUser =
                step("Зарегистрировать нового пользователя",
                        () -> api.users.register(new RegistrationBodyModel(username, password)));

        String accessToken = step("Авторизоваться и получить access токен",
                () -> api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password)));

        String firstName = "Anna";
        UpdateUserFirstNameBodyModel updateData = new UpdateUserFirstNameBodyModel(firstName);

        SuccessfulRegistrationResponseModel updateResponse =
                step("Частично обновить данные пользователя",
                        () -> api.users.updateUserPartial(accessToken, updateData));

        step("Проверить частично обновлённые данные пользователя", () -> {
            assertThat(updateResponse.id()).isEqualTo(registeredUser.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo("");
            assertThat(updateResponse.email()).isEqualTo("");
        });
    }

    @Test
    public void updateUserWithoutTokenTest() {
        UpdateUserBodyModel updateData =
                new UpdateUserBodyModel("Ivan", "Petrov", "ivan.petrov@example.com");

        WrongCredentialsLoginResponseModel response =
                step("Обновить пользователя без токена",
                        () -> api.users.updateUserWithoutToken(updateData));

        step("Проверить ошибку отсутствия авторизации",
                () -> assertThat(response.detail()).isEqualTo(UPDATE_USER_UNAUTHORIZED_ERROR));
    }

    @Test
    public void updateUserWithInvalidTokenTest() {
        UpdateUserBodyModel updateData =
                new UpdateUserBodyModel("Ivan", "Petrov", "ivan.petrov@example.com");

        UpdateUserInvalidTokenResponseModel response =
                step("Обновить пользователя с некорректным токеном",
                        () -> api.users.updateUserWithInvalidToken(updateData));

        step("Проверить ошибку некорректного токена", () -> {
            assertThat(response.detail()).isEqualTo(UPDATE_USER_INVALID_TOKEN_ERROR);
            assertThat(response.code()).isEqualTo(UPDATE_USER_INVALID_TOKEN_CODE);
        });
    }

    @Test
    public void replaceUserWithPartialBodyTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        step("Зарегистрировать нового пользователя",
                () -> api.users.register(new RegistrationBodyModel(username, password)));

        String accessToken = step("Авторизоваться и получить access токен",
                () -> api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password)));
        UpdateUserPutUsernameOnlyBodyModel updateData = new UpdateUserPutUsernameOnlyBodyModel(username);

        UpdateUserValidationErrorResponseModel response =
                step("Полностью обновить пользователя с неполным телом запроса",
                        () -> api.users.replaceUserWithValidationError(accessToken, updateData));

        step("Проверить ошибки обязательных полей", () -> {
            assertThat(response.firstName()).containsExactly(UPDATE_USER_REQUIRED_FIELD_ERROR);
            assertThat(response.lastName()).containsExactly(UPDATE_USER_REQUIRED_FIELD_ERROR);
            assertThat(response.email()).containsExactly(UPDATE_USER_REQUIRED_FIELD_ERROR);
        });
    }
}
