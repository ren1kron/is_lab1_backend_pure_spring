package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.model.OrganizationType;

public record OrgCreateDTO(
        String name,
        Long coordinatesId,
        Long officialAddressId,
        Long postalAddressId,
        Long annualTurnover,
        Long employeesCount,
        int rating,
        OrganizationType type
) {}
