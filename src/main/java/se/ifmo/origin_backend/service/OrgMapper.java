package se.ifmo.origin_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.dto.OrgCreateDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.repo.AddressRepo;
import se.ifmo.origin_backend.repo.CoordinatesRepo;
import se.ifmo.origin_backend.repo.LocationRepo;

@Component
@RequiredArgsConstructor
public class OrgMapper {
    private final CoordinatesRepo cordRepo;
    private final AddressRepo addrRepo;
    private final LocationRepo locRepo;

    public Organization dtoToOrg(OrgCreateDTO dto) {
        return dtoToOrg(dto, new Organization());
    }

    public Organization dtoToOrg(OrgCreateDTO dto, Organization org) {
        org.setName(dto.name());
        org.setCoordinates(cordRepo.findById(dto.coordinatesId())
            .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", dto.coordinatesId())));
        org.setOfficialAddress(locRepo.findById(dto.officialAddressId())
            .orElseThrow(() -> new NotFoundElementWithIdException("Location", dto.officialAddressId())));
        org.setAnnualTurnover(dto.annualTurnover());
        org.setEmployeesCount(dto.employeesCount());
        org.setRating(dto.rating());
        org.setType(dto.type());
        org.setPostalAddress(addrRepo.findById(dto.postalAddressId())
            .orElseThrow(() -> new NotFoundElementWithIdException("Address", dto.postalAddressId())));
        return org;
    }

    // public Organization rowToOrg(OrgImportDTO dto) {
    // return rowToOrg(dto, new Organization());
    // }
    //
    // public Organization rowToOrg(OrgImportDTO dto, Organization org) {
    // org.setName(dto.name());
    // org.setCoordinates(cordRepo.findById(dto.coordinatesId())
    // .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", dto.coordinatesId())));
    // org.setOfficialAddress(locRepo.findById(dto.officialAddressId())
    // .orElseThrow(() -> new NotFoundElementWithIdException("Location", dto.officialAddressId())));
    // org.setAnnualTurnover(dto.annualTurnover());
    // org.setEmployeesCount(dto.employeesCount());
    // org.setRating(dto.rating());
    // org.setType(dto.type());
    // org.setPostalAddress(addrRepo.findById(dto.postalAddressId())
    // .orElseThrow(() -> new NotFoundElementWithIdException("Address", dto.postalAddressId())));
    // return org;
    // }
}
