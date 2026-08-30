package models.logout;

import java.util.List;

public record LogoutValidationErrorResponseModel(List<String> refresh) {}
