package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationPage {

    private final SelenideElement usernameInput = $("[data-testid='username-input']");
    private final SelenideElement passwordInput = $("[data-testid='password-input']");
    private final SelenideElement confirmPasswordInput = $("[data-testid='confirm-password-input']");
    private final SelenideElement signupButton = $("[data-testid='signup-button']");
    private final SelenideElement errorMessage = $("[data-testid='error-message']");

    @Step("Открыть страницу регистрации")
    public RegistrationPage openPage() {
        open("/signup");
        return this;
    }

    @Step("Зарегистрироваться под именем {username}")
    public RegistrationPage register(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        confirmPasswordInput.setValue(password);
        signupButton.click();
        return this;
    }

    @Step("Проверить ошибку регистрации «{message}»")
    public RegistrationPage shouldHaveError(String message) {
        errorMessage.shouldHave(exactText(message));
        return this;
    }
}
