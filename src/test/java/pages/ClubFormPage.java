package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.clubs.ClubBodyModel;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class ClubFormPage {

    private final SelenideElement bookTitleInput = $("#bookTitle");
    private final SelenideElement bookAuthorsInput = $("#bookAuthors");
    private final SelenideElement publicationYearInput = $("#publicationYear");
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement telegramChatLinkInput = $("#telegramChatLink");
    private final SelenideElement saveButton = $x("//button[normalize-space()='Сохранить изменения']");

    @Step("Открыть редактирование клуба с id={clubId}")
    public ClubFormPage openEditPage(Integer clubId, String currentBookTitle) {
        open("/clubs/" + clubId + "/edit");
        bookTitleInput.shouldHave(value(currentBookTitle));
        return this;
    }

    @Step("Изменить данные клуба")
    public ClubsPage editClub(ClubBodyModel club) {
        bookTitleInput.setValue(club.bookTitle());
        bookAuthorsInput.setValue(club.bookAuthors());
        publicationYearInput.setValue(club.publicationYear().toString());
        descriptionInput.setValue(club.description());
        telegramChatLinkInput.setValue(club.telegramChatLink());
        saveButton.click();
        return new ClubsPage();
    }
}
