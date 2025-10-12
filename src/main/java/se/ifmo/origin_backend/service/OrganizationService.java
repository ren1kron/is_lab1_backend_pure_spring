package se.ifmo.origin_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate broker;

    public record OrgEvent(String type, int id) {}

    @Transactional(readOnly = true)
    public List<OrgFullDTO> getAll() {
        return orgRepo.findAllFull();
    }

    @Transactional(readOnly = true)
    public OrgFullDTO getById(int id) {
        return orgRepo.findFullById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));
    }

    @Transactional
    public void create(OrgCreateDTO dto) {
        var org = dtoToOrg(dto);
        var saved = orgRepo.save(org);
        broker.convertAndSend("/topic/org-changed", new OrgEvent("CREATED", saved.getId()));
    }

    @Transactional
    public void update(int id, OrgCreateDTO dto) {
        Organization org = orgRepo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));

        dtoToOrg(dto, org);
        var saved = orgRepo.save(org);
        broker.convertAndSend("/topic/org-changed", new OrgEvent("UPDATED", saved.getId()));
    }

    @Transactional
    public void delete(int id) {
        if (orgRepo.findById(id).isEmpty()) throw new NotFoundElementWithIdException("Organization", id);
        orgRepo.deleteById(id);
        broker.convertAndSend("/topic/org-changed", new OrgEvent("CREATED", id));
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
                o.getId(),
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
