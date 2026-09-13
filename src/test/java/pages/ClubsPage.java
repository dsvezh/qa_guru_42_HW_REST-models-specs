package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class ClubsPage {

    private final SelenideElement searchInput = $("input[placeholder='Поиск книжных клубов...']");
    private final SelenideElement searchButton = $x("//button[normalize-space()='Найти']");
    private final ElementsCollection clubCards = $$(".club-card");
    private final String cardTitle = "h2";
    private final String cardAuthors = ".authors";
    private final String cardYear = ".year";
    private final String cardDescription = ".description";

    @Step("Открыть каталог клубов")
    public ClubsPage openPage() {
        open("/");
        return this;
    }

    @Step("Найти клуб «{bookTitle}»")
    public ClubsPage search(String bookTitle) {
        searchInput.setValue(bookTitle);
        searchButton.click();
        clubCards.shouldHave(size(1));
        return this;
    }

    @Step("Проверить карточку клуба «{bookTitle}»")
    public ClubsPage shouldContainClub(
            String bookTitle,
            String authors,
            Integer publicationYear,
            String description) {
        SelenideElement card = cardByTitle(bookTitle).shouldBe(visible);
        card.$(cardTitle).shouldHave(exactText(bookTitle));
        card.$(cardAuthors).shouldHave(exactText(authors));
        card.$(cardYear).shouldHave(exactText(publicationYear.toString()));
        card.$(cardDescription).shouldHave(text(description));
        return this;
    }

    private SelenideElement cardByTitle(String bookTitle) {
        return clubCards.findBy(text(bookTitle));
    }
}
