package se.ifmo.origin_backend.dto;

import java.time.LocalDate;
import se.ifmo.origin_backend.model.OrganizationType;

public record OrgFullDTO(
    Integer id,
    String name,
    CoordinatesDTO coordinates,
    LocalDate creationDate,
    LocationDTO officialAddress,
    Long annualTurnover,
    Long employeesCount,
    int rating,
    OrganizationType type,
    AddressDTO postalAddress) {
    public OrgFullDTO(
        Integer id,
        String name,
        Long coordX,
        Long coordY,
        LocalDate creationDate,
        float locX,
        int locY,
        float locZ,
        String locName,
        Long annualTurnover,
        Long employeesCount,
        int rating,
        OrganizationType type,
        String street) {
        this(id, name, new CoordinatesDTO(coordX, coordY), creationDate,
            new LocationDTO(locX, locY, locZ, locName),
            annualTurnover,
            employeesCount,
            rating, type,
            new AddressDTO(street));
    }
}
