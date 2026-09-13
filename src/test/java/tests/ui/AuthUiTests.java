package tests.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.RegistrationPage;
import tests.TestBase;
import tests.fixtures.ApiFixture;
import tests.fixtures.ApiFixture.TestUser;

@Tag("ui")
public class AuthUiTests extends TestBase {

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
    @DisplayName("Пользователь, подготовленный через API, может войти через UI")
    void successfulLoginWithApiPreparedUser() {
        TestUser user = fixture.createUser("login");

        new LoginPage()
                .openPage()
                .login(user.username(), user.password());

        new LoginPage().shouldBeAuthenticated();
    }

    @Test
    @DisplayName("UI показывает ошибку регистрации для username, занятого через API")
    void registrationWithApiPreparedExistingUsername() {
        TestUser existingUser = fixture.createUser("duplicate");

        new RegistrationPage()
                .openPage()
                .register(existingUser.username(), existingUser.password())
                .shouldHaveError("Ошибка при регистрации");
    }
}
