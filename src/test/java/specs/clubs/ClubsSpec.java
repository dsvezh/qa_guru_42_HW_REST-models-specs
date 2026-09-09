package specs.clubs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class ClubsSpec {

    public static RequestSpecification clubsRequestSpec = baseRequestSpec;

    public static RequestSpecification authorizedClubsRequestSpec(String accessToken) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseRequestSpec)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public static ResponseSpecification successfulClubsListResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/clubs_list_response_schema.json"))
            .expectBody("count", notNullValue())
            .expectBody("count", greaterThanOrEqualTo(0))
            .expectBody("results", notNullValue())
            .build();

    public static ResponseSpecification successfulClubReviewsListResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(200)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/club_reviews_list_response_schema.json"))
                    .expectBody("count", notNullValue())
                    .expectBody("count", greaterThanOrEqualTo(0))
                    .expectBody("results", notNullValue())
                    .build();

    public static ResponseSpecification clubReviewsInvalidPageResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(404)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/club_reviews_invalid_page_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification successfulCreateClubReviewResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(201)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/successful_create_club_review_response_schema.json"))
                    .expectBody("id", notNullValue())
                    .expectBody("user", notNullValue())
                    .build();

    public static ResponseSpecification createClubReviewUnauthorizedResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(401)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/login/wrong_credentials_login_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification createClubReviewValidationErrorResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(400)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/create_club_review_validation_error_response_schema.json"))
                    .expectBody("assessment", notNullValue())
                    .build();

    public static ResponseSpecification successfulUpdateClubReviewResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(200)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/successful_update_club_review_response_schema.json"))
                    .expectBody("id", notNullValue())
                    .expectBody("user", notNullValue())
                    .expectBody("modified", notNullValue())
                    .build();

    public static ResponseSpecification updateClubReviewUnauthorizedResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(401)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/login/wrong_credentials_login_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification updateClubReviewForbiddenResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(403)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/login/wrong_credentials_login_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification updateClubReviewValidationErrorResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(400)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/update_club_review_validation_error_response_schema.json"))
                    .expectBody("assessment", notNullValue())
                    .build();

    public static ResponseSpecification successfulDeleteClubReviewResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(204)
                    .expectBody(is(emptyOrNullString()))
                    .build();

    public static ResponseSpecification deleteClubReviewUnauthorizedResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(401)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/login/wrong_credentials_login_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification deleteClubReviewForbiddenResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(403)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/login/wrong_credentials_login_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification deleteClubReviewNotFoundResponseSpec =
            new ResponseSpecBuilder()
                    .log(ALL)
                    .expectStatusCode(404)
                    .expectBody(matchesJsonSchemaInClasspath(
                            "schemas/clubs/delete_club_review_not_found_response_schema.json"))
                    .expectBody("detail", notNullValue())
                    .build();

    public static ResponseSpecification successfulCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_create_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("owner", notNullValue())
            .build();

    public static ResponseSpecification createClubUnauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/wrong_credentials_login_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification createClubValidationErrorResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/create_club_validation_error_response_schema.json"))
            .expectBody("bookTitle", notNullValue())
            .build();

    public static ResponseSpecification successfulUpdateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_update_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("modified", notNullValue())
            .build();

    public static ResponseSpecification updateClubUnauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/wrong_credentials_login_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification updateClubValidationErrorResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/update_club_validation_error_response_schema.json"))
            .expectBody("bookTitle", notNullValue())
            .build();

    public static ResponseSpecification successfulDeleteClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .expectBody(is(emptyOrNullString()))
            .build();

    public static ResponseSpecification deleteClubUnauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/login/wrong_credentials_login_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification deleteClubNotFoundResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/delete_club_not_found_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();
}
