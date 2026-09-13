package tests.fixtures;

import models.clubs.ClubBodyModel;

import java.util.UUID;

public final class UiTestDataFactory {

    private UiTestDataFactory() {
    }

    public static Credentials credentials(String purpose) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return new Credentials("ui_" + purpose + "_" + suffix, "pass_" + suffix);
    }

    public static ClubBodyModel club(String purpose) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return new ClubBodyModel(
                "UI " + purpose + " " + suffix,
                "QA Guru Author " + suffix,
                2026,
                "Клуб подготовлен через API для UI-теста " + suffix,
                "https://t.me/ui_" + suffix);
    }

    public record Credentials(String username, String password) {
    }
}
