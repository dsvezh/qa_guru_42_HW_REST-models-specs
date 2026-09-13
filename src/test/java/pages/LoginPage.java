package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement usernameInput = $("input[placeholder='Введите логин']");
    private final SelenideElement passwordInput = $("input[placeholder='Введите пароль']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement profileLink = $("[data-testid='profile-link']");
    private final SelenideElement createClubLink = $("[data-testid='create-club-link']");

    @Step("Открыть страницу входа")
    public LoginPage openPage() {
        open("/signin");
        return this;
    }

    @Step("Войти под пользователем {username}")
    public ClubsPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        profileLink.shouldBe(visible);
        return new ClubsPage();
    }

    @Step("Проверить, что пользователь авторизован")
    public LoginPage shouldBeAuthenticated() {
        profileLink.shouldBe(visible);
        createClubLink.shouldBe(visible);
        return this;
    }
}
