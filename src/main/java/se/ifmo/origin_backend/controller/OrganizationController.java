package se.ifmo.origin_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.OrgCreateDTO;
import se.ifmo.origin_backend.dto.OrgSearchRequestDTO;
import se.ifmo.origin_backend.dto.PageDTO;
import se.ifmo.origin_backend.event.OrgEvent;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.model.OrganizationType;
import se.ifmo.origin_backend.service.OrganizationService;

@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
public class OrganizationController {
    private static final Map<String, String> SORT_MAP =
        Map.ofEntries(Map.entry("name", "name"), Map.entry("employeesCount", "employeesCount"), Map.entry("rating", "rating"), Map
            .entry("coordinatesX", "coordinates.x"), Map.entry("coordinatesY", "coordinates.y"), Map
                .entry("officialAddressX", "officialAddress.x"), Map.entry("officialAddressY", "officialAddress.y"), Map
                    .entry("officialAddressZ", "officialAddress.z"), Map.entry("postalStreet", "postalAddress.street"), Map
                        .entry("annualTurnover", "annualTurnover"), Map.entry("type", "type"));

    private final OrganizationService service;
    private final ApplicationEventPublisher events;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Organization create(@RequestBody OrgCreateDTO dto) {
        var created = service.create(dto);
        events.publishEvent(new OrgEvent("CREATED", created.getId()));
        return created;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Organization update(@PathVariable int id, @RequestBody OrgCreateDTO dto) {
        var updated = service.update(id, dto);
        events.publishEvent(new OrgEvent("UPDATED", updated.getId()));
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
        events.publishEvent(new OrgEvent("DELETED", id));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        service.clear();
        events.publishEvent(new OrgEvent("CLEARED_ALL", 0));
    }

    @GetMapping
    public PageDTO<Organization> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort, // "field,asc|desc"
        @RequestParam(required = false) String name,
        @RequestParam(required = false) List<OrganizationType> type,
        @RequestParam(required = false) Long employeesMin,
        @RequestParam(required = false) Long employeesMax,
        @RequestParam(required = false) Long annualTurnoverMin,
        @RequestParam(required = false) Long annualTurnoverMax,
        @RequestParam(required = false) Integer ratingMin,
        @RequestParam(required = false) Integer ratingMax,
        @RequestParam(required = false) Long coordXMin,
        @RequestParam(required = false) Long coordXMax,
        @RequestParam(required = false) Long coordYMin,
        @RequestParam(required = false) Long coordYMax,
        @RequestParam(required = false) Float locationXMin,
        @RequestParam(required = false) Float locationXMax,
        @RequestParam(required = false) Integer locationYMin,
        @RequestParam(required = false) Integer locationYMax,
        @RequestParam(required = false) Float locationZMin,
        @RequestParam(required = false) Float locationZMax,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo) {
        OrgSearchRequestDTO req = new OrgSearchRequestDTO(name, type, employeesMin, employeesMax, annualTurnoverMin, annualTurnoverMax,
            ratingMin, ratingMax, createdFrom, createdTo, coordXMin, coordXMax, coordYMin, coordYMax, locationXMin, locationXMax,
            locationYMin, locationYMax, locationZMin, locationZMax);

        // Whitelist sort fields to entity props
        Sort sortSpec = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] p = sort.split(",");
            String requested = p[0].strip();
            Sort.Direction dir = (p.length > 1 && "desc".equalsIgnoreCase(p[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            String property = SORT_MAP.getOrDefault(requested, "creationDate");
            sortSpec = Sort.by(new Sort.Order(dir, property));
        }

        Pageable pageable = PageRequest.of(page, size, sortSpec);
        return service.search(req, pageable);
    }

    @GetMapping("/{id}")
    public Organization getOrganizationById(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/task1/{addressId}")
    public Long countAllByPostalAddressLessThan(@PathVariable Long addressId) {
        return service.countAllByPostalAddressLessThan(addressId);
    }

    @GetMapping("/task2/{addressId}")
    public List<Organization> findAllByPostalAddressGreaterThan(@PathVariable Long addressId) {
        return service.findAllByPostalAddressGreaterThan(addressId);
    }

    @GetMapping("/task3")
    public List<OrganizationType> findDistinctTypes() {
        return service.findDistinctTypes();
    }

    @GetMapping("/task4")
    public List<Organization> findTop5OrgsWithGreatestAnnualTurnover() {
        return service.findTop5OrgsWithGreatestAnnualTurnover();
    }

    @GetMapping("/task5")
    public Double findAvgEmployeesFor10OrgsWithGreatestAnnualTurnover() {
        return service.findAvgEmployeesFor10OrgsWithGreatestAnnualTurnover();
    }
}

