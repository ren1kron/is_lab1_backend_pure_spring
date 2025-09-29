package se.ifmo.origin_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.*;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.repo.AddressRepo;
import se.ifmo.origin_backend.repo.CoordinatesRepo;
import se.ifmo.origin_backend.repo.LocationRepo;
import se.ifmo.origin_backend.repo.OrganizationRepo;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationService {
    private final OrganizationRepo orgRepo;
    private final CoordinatesRepo cordRepo;
    private final AddressRepo addrRepo;
    private final LocationRepo locRepo;

    @Transactional(readOnly = true)
    public List<OrgFullDTO> getOrganizations() {
        return orgRepo.findAllFull();
    }

    @Transactional(readOnly = true)
    public OrgFullDTO getOrganizationById(int id) {
        return orgRepo.findFullById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));
    }

    @Transactional
    public void addOrganization(OrgCreateDTO dto) {
        var org = dtoToOrg(dto);

        orgRepo.save(org);
    }

    @Transactional
    public void updateOrganization(int id, OrgCreateDTO dto) {
        Organization org = orgRepo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));

        dtoToOrg(dto, org);
        orgRepo.save(org);
    }

    @Transactional
    public void deleteOrganization(int id) {
        if (orgRepo.findById(id).isEmpty()) throw new NotFoundElementWithIdException("Organization", id);
        orgRepo.deleteById(id);
    }

    @Transactional
    public void clear() {
        orgRepo.deleteAll();
        cordRepo.deleteAll();
        addrRepo.deleteAll();
        locRepo.deleteAll();
    }

    // –––––––––––––––––––––––––––––––––––––––––

    private Organization dtoToOrg(OrgCreateDTO dto) {
        return dtoToOrg(dto, new Organization());
    }

    private Organization dtoToOrg(OrgCreateDTO dto, Organization org) {
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

    private OrgFullDTO orgToFullDto(Organization o) {
        return new OrgFullDTO(
                o.getName(),
                new CoordinatesDTO(o.getCoordinates().getX(), o.getCoordinates().getY()),
                o.getCreationDate(),
                new LocationDTO(o.getOfficialAddress().getX(), o.getOfficialAddress().getY(), o.getOfficialAddress().getZ(), o.getOfficialAddress().getName()),
                o.getAnnualTurnover(),
                o.getEmployeesCount(),
                o.getRating(),
                o.getType(),
                new AddressDTO(o.getPostalAddress().getStreet())
        );
    }
}
