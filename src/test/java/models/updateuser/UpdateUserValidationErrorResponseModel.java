package models.updateuser;

import java.util.List;

public record UpdateUserValidationErrorResponseModel(
        List<String> username,
        List<String> firstName,
        List<String> lastName,
        List<String> email
) {}
