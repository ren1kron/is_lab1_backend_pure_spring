package se.ifmo.origin_backend.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.OrgCreateDTO;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.service.OrganizationService;

@RestController
@RequestMapping("/orgs")
public class OrganizationController {
    // todo make getOrganizations send ids of coords, addrs and locs

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Organization> getOrgranizations() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Organization getOrganizationById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public void addOrganization(@RequestBody OrgCreateDTO dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void updateOrganization(@PathVariable int id, @RequestBody OrgCreateDTO dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable int id) {
        service.delete(id);
    }

    @DeleteMapping
    public void clear() {
        service.clear();
    }
}
