package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.*;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.model.OrganizationType;
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

    private final OrgSpecFactory orgSpecFactory;

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

    // Вернуть количество объектов, значение поля postalAddress которых меньше заданного.
    @Transactional(readOnly = true)
    public Long countAllByPostalAddressLessThan(Long addressId) {
        var addr = addrRepo.findById(addressId)
            .orElseThrow(() -> new NotFoundElementWithIdException("Address", addressId));
        return orgRepo.countAllByPostalAddress_StreetLessThan(addr.getStreet());
    }

    // Вернуть массив объектов, значение поля postalAddress которых больше заданного.
    @Transactional(readOnly = true)
    public List<Organization> findAllByPostalAddressGreaterThan(Long addressId) {
        var addr = addrRepo.findById(addressId)
            .orElseThrow(() -> new NotFoundElementWithIdException("Address", addressId));
        return orgRepo.findAllByPostalAddress_StreetGreaterThan(addr.getStreet());
    }

    // Вернуть массив объектов, значение поля postalAddress которых больше заданного
    @Transactional(readOnly = true)
    public List<OrganizationType> findDistinctTypes() {
        return orgRepo.findDistinctTypes();
    }

    // Вывести 5 организаций с максимальным годовым оборотом
    @Transactional(readOnly = true)
    public List<Organization> findTop5OrgsWithGreatestAnnualTurnover() {
        Pageable pageTop5 = PageRequest.of(0, 5, Sort.by("annualTurnover").descending());
        return orgRepo.findAll(pageTop5).getContent();
    }

    // Найти среднее количество сотрудников для 10 крупнейших организаций по годовому обороту
    @Transactional(readOnly = true)
    public Double findAvgEmployeesFor10OrgsWithGreatestAnnualTurnover() {
        Pageable pageTop10 = PageRequest.of(0, 10, Sort.by("annualTurnover").descending());
        Double avg = orgRepo.findAll(pageTop10).stream()
            .mapToLong(Organization::getEmployeesCount)
            .average()
            .orElse(0.0);
        return avg;
    }

    @Transactional
    public Organization create(OrgCreateDTO dto) {
        var org = dtoToOrg(dto);
        return orgRepo.save(org);
    }

    @Transactional
    public Organization update(int id, OrgCreateDTO dto) {
        Organization org = orgRepo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Organization", id));

        dtoToOrg(dto, org);
        return orgRepo.save(org);
    }

    @Transactional
    public void delete(int id) {
        if (orgRepo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Organization", id);
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
}
