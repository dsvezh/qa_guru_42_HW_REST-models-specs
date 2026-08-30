package models.login;

import java.util.List;

public record LoginValidationErrorResponseModel(List<String> username, List<String> password) {}
