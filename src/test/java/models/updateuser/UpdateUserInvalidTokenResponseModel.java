package models.updateuser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateUserInvalidTokenResponseModel(String detail, String code) {}
