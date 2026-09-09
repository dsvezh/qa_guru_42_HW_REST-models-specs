package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "user8";
    public static final String LOGIN_PASSWORD = "user8";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";
    public static final String LOGIN_WRONG_USERNAME = "nonexistent_user";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String BLANK_FIELD_ERROR = "This field may not be blank.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
            "A user with that username already exists.";

    public static final String LOGOUT_INVALID_REFRESH_TOKEN = "invalid_refresh_token";
    public static final String LOGOUT_INVALID_TOKEN_ERROR = "Token is invalid";
    public static final String LOGOUT_TOKEN_BLACKLISTED_ERROR = "Token is blacklisted";
    public static final String INVALID_TOKEN_CODE = "token_not_valid";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static final String UNAUTHORIZED_ERROR =
            "Authentication credentials were not provided.";
    public static final String FORBIDDEN_ERROR =
            "You do not have permission to perform this action.";
    public static final String UPDATE_USER_INVALID_ACCESS_TOKEN = "invalid_access_token";
    public static final String UPDATE_USER_INVALID_TOKEN_ERROR =
            "Given token not valid for any token type";
    public static final String REQUIRED_FIELD_ERROR = "This field is required.";

    public static final String CLUB_REVIEWS_INVALID_PAGE_ERROR = "Invalid page.";
    public static final String MAX_ASSESSMENT_ERROR =
            "Ensure this value is less than or equal to 5.";
    public static final String DELETE_CLUB_REVIEW_NOT_FOUND_ERROR =
            "No BookReview matches the given query.";

    public static final String DELETE_CLUB_NOT_FOUND_ERROR =
            "No Club matches the given query.";
}
