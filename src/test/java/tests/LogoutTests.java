package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutInvalidTokenResponseModel;
import models.logout.LogoutValidationErrorResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.LOGOUT_BLANK_FIELD_ERROR;
import static tests.TestData.LOGOUT_INVALID_REFRESH_TOKEN;
import static tests.TestData.LOGOUT_INVALID_TOKEN_CODE;
import static tests.TestData.LOGOUT_INVALID_TOKEN_ERROR;
import static tests.TestData.LOGOUT_TOKEN_BLACKLISTED_ERROR;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        String refreshToken =
                api.auth.loginAndGetRefreshToken(new LoginBodyModel(username, password));

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @Test
    public void invalidRefreshTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(LOGOUT_INVALID_REFRESH_TOKEN);

        LogoutInvalidTokenResponseModel logoutResponse =
                api.auth.logoutWithInvalidToken(logoutData);

        step("Проверить ошибку некорректного токена", () -> {
            assertThat(logoutResponse.detail()).isEqualTo(LOGOUT_INVALID_TOKEN_ERROR);
            assertThat(logoutResponse.code()).isEqualTo(LOGOUT_INVALID_TOKEN_CODE);
        });
    }

    @Test
    public void emptyRefreshTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        LogoutValidationErrorResponseModel logoutResponse =
                api.auth.logoutWithValidationError(logoutData);

        step("Проверить ошибку обязательного поля refresh",
                () -> assertThat(logoutResponse.refresh()).containsExactly(LOGOUT_BLANK_FIELD_ERROR));
    }

    @Test
    public void repeatedLogoutTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);

        LogoutInvalidTokenResponseModel secondLogoutResponse =
                api.auth.logoutWithInvalidToken(logoutData);

        step("Проверить ошибку заблокированного токена", () -> {
            assertThat(secondLogoutResponse.detail()).isEqualTo(LOGOUT_TOKEN_BLACKLISTED_ERROR);
            assertThat(secondLogoutResponse.code()).isEqualTo(LOGOUT_INVALID_TOKEN_CODE);
        });
    }
}
