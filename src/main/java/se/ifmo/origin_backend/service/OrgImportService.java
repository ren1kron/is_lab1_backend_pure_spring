package se.ifmo.origin_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.*;
import se.ifmo.origin_backend.error.DuplicateOrganizationException;
import se.ifmo.origin_backend.error.ImportValidationException;
import se.ifmo.origin_backend.error.RowError;
import se.ifmo.origin_backend.event.OrgBulkEvent;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.repo.AddressRepo;
import se.ifmo.origin_backend.repo.CoordinatesRepo;
import se.ifmo.origin_backend.repo.LocationRepo;
import se.ifmo.origin_backend.repo.OrganizationRepo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrgImportService {
    private final OrganizationRepo orgRepo;
    private final CoordinatesRepo cordRepo;
    private final AddressRepo addrRepo;
    private final LocationRepo locRepo;
    private final Validator validator;

    private final ObjectMapper jsonMapper;
    private final ApplicationEventPublisher events;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ImportResult importOrganizations(InputStream jsonStream) throws IOException {
        List<OrgImportDTO> dtos = readJson(jsonStream);

        List<RowError> errors = new ArrayList<>();
        List<Organization> toPersist = new ArrayList<>();
        Set<String> orgNameCache = new HashSet<>();

        // per-import caches
        Map<LocationDTO, Location> locCache = new HashMap<>();
        Map<AddressDTO, Address> addrCache = new HashMap<>();
        Map<CoordinatesDTO, Coordinates> cordCache = new HashMap<>();

        int rowNum = 1;
        for (OrgImportDTO dto : dtos) {
            Set<ConstraintViolation<OrgImportDTO>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                errors.add(RowError.fromViolations(rowNum, violations));
                rowNum++;
                continue;
            }

            try {
                if (orgRepo.existsByName(dto.name())) {
                    throw new DuplicateOrganizationException(dto.name());
                }
                Organization org = rowToOrg(dto, locCache, addrCache, cordCache);
                if (!orgNameCache.add(dto.name())) {
                    throw new DuplicateOrganizationException(dto.name());
                }
                toPersist.add(org);
            } catch (RuntimeException ex) {
                errors.add(new RowError(rowNum, ex.getMessage()));
            }

            rowNum++;
        }

        if (!errors.isEmpty()) {
            throw new ImportValidationException(errors);
        }

        List<Organization> saved = orgRepo.saveAll(toPersist);

        List<Integer> ids = saved.stream()
            .map(Organization::getId)
            .toList();
        events.publishEvent(new OrgBulkEvent("IMPORTED", ids));

        return new ImportResult(toPersist.size());
    }

    private List<OrgImportDTO> readJson(InputStream jsonStream) throws IOException {
        return jsonMapper.readValue(jsonStream, jsonMapper.getTypeFactory()
            .constructCollectionType(List.class, OrgImportDTO.class));
    }

    private Organization rowToOrg(
        OrgImportDTO dto,
        Map<LocationDTO, Location> locCache,
        Map<AddressDTO, Address> addrCache,
        Map<CoordinatesDTO, Coordinates> cordCache) {
        // resolve location (off addr)
        LocationDTO locKey = dto.location();
        Location loc =
            locCache.computeIfAbsent(locKey, key -> locRepo.findByXAndYAndZAndName(key.x(), key.y(), key.z(), key.name()).orElseGet(() -> {
                Location l = new Location();
                l.setX(key.x());
                l.setY(key.y());
                l.setZ(key.z());
                l.setName(key.name());
                return locRepo.save(l);
            }));
        // resolve address (postal addr)
        AddressDTO addrDto = dto.address();
        Address addr = addrCache.computeIfAbsent(addrDto, key -> addrRepo.findByStreet(addrDto.street())
            .orElseGet(() -> {
                Address a = new Address();
                a.setStreet(addrDto.street());
                return addrRepo.save(a);
            }));

        // resolve coordinates
        CoordinatesDTO cordsDto = dto.coordinates();
        Coordinates cords =
            cordCache.computeIfAbsent(cordsDto, key -> cordRepo.findCoordinatesByXAndY(cordsDto.x(), cordsDto.y()).orElseGet(() -> {
                Coordinates c = new Coordinates();
                c.setX(cordsDto.x());
                c.setY(cordsDto.y());
                return cordRepo.save(c);
            }));

        Organization org = new Organization();
        org.setCoordinates(cords);
        org.setOfficialAddress(loc);
        org.setPostalAddress(addr);
        org.setName(dto.name());
        org.setAnnualTurnover(dto.annualTurnover());
        org.setEmployeesCount(dto.employeesCount());
        org.setRating(dto.rating());
        org.setType(dto.type());
        return org;
    }
}
