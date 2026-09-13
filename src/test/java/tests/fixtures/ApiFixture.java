package tests.fixtures;

import api.ApiClient;
import models.clubs.ClubBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubReviewBodyModel;
import models.clubs.ClubReviewModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;

public class ApiFixture {

    private final ApiClient api;
    private ClubModel createdClub;
    private String accessToken;

    public ApiFixture(ApiClient api) {
        this.api = api;
    }

    public TestUser createUser(String purpose) {
        UiTestDataFactory.Credentials credentials = UiTestDataFactory.credentials(purpose);
        SuccessfulRegistrationResponseModel user = api.users.register(
                new RegistrationBodyModel(credentials.username(), credentials.password()));
        SuccessfulLoginResponseModel tokens = api.auth.login(
                new LoginBodyModel(credentials.username(), credentials.password()));

        return new TestUser(
                user.id(),
                credentials.username(),
                credentials.password(),
                tokens.access(),
                tokens.refresh());
    }

    public ClubModel createClub(TestUser owner, ClubBodyModel body) {
        createdClub = api.clubs.createClub(owner.accessToken(), body);
        accessToken = owner.accessToken();
        return createdClub;
    }

    public ClubReviewModel createReview(
            TestUser author,
            Integer clubId,
            String review,
            Integer assessment,
            Integer readPages) {
        return api.clubs.createClubReview(
                author.accessToken(),
                new ClubReviewBodyModel(clubId, review, assessment, readPages));
    }

    public void close() {
        if (createdClub != null) {
            api.clubs.deleteClub(accessToken, createdClub.id());
        }
    }

    public record TestUser(
            Integer id,
            String username,
            String password,
            String accessToken,
            String refreshToken) {
    }
}
