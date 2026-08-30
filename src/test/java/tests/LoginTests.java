package tests;

import models.login.LoginBodyModel;
import models.login.LoginValidationErrorResponseModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();
        api.users.register(new RegistrationBodyModel(username, password));

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();
        assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        assertThat(loginResponse.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @Test
    public void wrongUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_WRONG_USERNAME, LOGIN_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        assertThat(loginResponse.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @Test
    public void emptyUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_PASSWORD);

        LoginValidationErrorResponseModel loginResponse = api.auth.loginWithValidationError(loginData);

        assertThat(loginResponse.username()).containsExactly(LOGIN_BLANK_FIELD_ERROR);
    }

    @Test
    public void emptyPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, "");

        LoginValidationErrorResponseModel loginResponse = api.auth.loginWithValidationError(loginData);

        assertThat(loginResponse.password()).containsExactly(LOGIN_BLANK_FIELD_ERROR);
    }

}
