package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.*;
import se.ifmo.origin_backend.model.OrganizationType;

public record OrgCreateDTO(
    @NotBlank @Size(max = 63) String name,
    @NotNull Long coordinatesId,
    @NotNull Long officialAddressId,
    @NotNull Long postalAddressId,
    @Positive Long annualTurnover,
    @NotNull @Positive Long employeesCount,
    @NotNull @Positive @Max(100) Integer rating,
    OrganizationType type) {}
