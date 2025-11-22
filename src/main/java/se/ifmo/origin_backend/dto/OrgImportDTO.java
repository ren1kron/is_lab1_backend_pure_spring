package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.*;
import se.ifmo.origin_backend.model.OrganizationType;

// public record OrgImportDTO(
// @NotBlank @Size(max = 63) String name,
// @Max(562) @NotNull Long cordX,
// @NotNull Long cordY,
// @NotNull Float locX,
// @NotNull Integer locY,
// @NotNull Float locZ,
// @NotBlank @Size(max = 63) String locName,
// @Size(max = 63) String postalStreet,
// @Positive Long annualTurnover,
// @NotNull @Positive Long employeesCount,
// @NotNull @Positive Integer rating,
// OrganizationType type) {}

public record OrgImportDTO(
    @NotBlank @Size(max = 63) String name,
    @NotNull CoordinatesDTO coordinates,
    @NotNull LocationDTO location,
    @NotNull AddressDTO address,
    @Positive Long annualTurnover,
    @NotNull @Positive Long employeesCount,
    @NotNull @Positive Integer rating,
    OrganizationType type) {}
