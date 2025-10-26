package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import se.ifmo.origin_backend.dto.*;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.repo.AddressRepo;
import se.ifmo.origin_backend.repo.CoordinatesRepo;
import se.ifmo.origin_backend.repo.LocationRepo;
import se.ifmo.origin_backend.repo.OrganizationRepo;
import se.ifmo.origin_backend.spec.OrgSpecFactory;

@Service
@AllArgsConstructor
public class OrganizationService {
    private final OrganizationRepo orgRepo;
    private final CoordinatesRepo cordRepo;
    private final AddressRepo addrRepo;
    private final LocationRepo locRepo;
    private final ApplicationEventPublisher events;

    private final OrgSpecFactory orgSpecFactory;

    public record OrgEvent(
        String type,
        int id) {}

    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return orgRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Organization getById(int id) {
        return orgRepo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));
    }

    @Transactional(readOnly = true)
    public PageDTO<Organization> search(OrgSearchRequestDTO req, Pageable pageable) {
        Specification<Organization> spec = orgSpecFactory.from(req);
        Page<Organization> page = orgRepo.findAll(spec, pageable);
        return new PageDTO<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional
    public Organization create(OrgCreateDTO dto) {
        var org = dtoToOrg(dto);
        var saved = orgRepo.save(org);
        events.publishEvent(new OrgEvent("CREATED", saved.getId()));
        return saved;
    }

    @Transactional
    public Organization update(int id, OrgCreateDTO dto) {
        Organization org = orgRepo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));

        dtoToOrg(dto, org);
        var saved = orgRepo.save(org);
        events.publishEvent(new OrgEvent("UPDATED", saved.getId()));
        return saved;
    }

    @Transactional
    public void delete(int id) {
        if (orgRepo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Organization", id);
        orgRepo.deleteById(id);
        events.publishEvent(new OrgEvent("DELETED", id));
    }

    @Transactional
    public void clear() {
        orgRepo.deleteAll();
        cordRepo.deleteAll();
        addrRepo.deleteAll();
        locRepo.deleteAll();
        events.publishEvent(new OrgEvent("CLEARED_ALL", 0));
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
}


@Component
@AllArgsConstructor
class OrgEventForwarder {
    private final SimpMessagingTemplate broker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrganizationService.OrgEvent e) {
        broker.convertAndSend("/topic/org-changed", e);
    }
}
