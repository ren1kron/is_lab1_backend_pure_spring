package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.model.OrganizationType;

import java.time.LocalDate;

public record OrgFullDTO(
        String name,
        CoordinatesDTO coordinates,
        LocalDate creationDate,
        LocationDTO officialAddress,
        Long annualTurnover,
        Long employeesCount,
        int rating,
        OrganizationType type,
        AddressDTO postalAddress
) {
    public OrgFullDTO(
            String name,
            Long coordX, Long coordY,
            LocalDate creationDate,
            float locX, int locY, float locZ, String locName,
            Long annualTurnover,
            Long employeesCount,
            int rating,
            OrganizationType type,
            String street
    ) {
        this(
                name,
                new CoordinatesDTO(coordX, coordY),
                creationDate,
                new LocationDTO(locX, locY, locZ, locName),
                annualTurnover,
                employeesCount,
                rating,
                type,
                new AddressDTO(street)
        );
    }
}
