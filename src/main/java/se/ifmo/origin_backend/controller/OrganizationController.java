package se.ifmo.origin_backend.controller;

import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.OrgCreateDTO;
import se.ifmo.origin_backend.dto.OrgFullDTO;
import se.ifmo.origin_backend.service.OrganizationService;

import java.util.List;

@RestController
@RequestMapping("/orgs")
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrgFullDTO> getOrgranizations() {
        return service.getOrganizations();
    }

    @GetMapping("/{id}")
    public OrgFullDTO getOrganizationById(@PathVariable int id) {
        return service.getOrganizationById(id);
    }

    @PostMapping
    public void addOrganization(@RequestBody OrgCreateDTO dto) {
        service.addOrganization(dto);
    }

    @PutMapping("/{id}")
    public void updateOrganization(@PathVariable int id, @RequestBody OrgCreateDTO dto) {
        service.updateOrganization(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable int id) {
        service.deleteOrganization(id);
    }

    @DeleteMapping
    public void clear() {
        service.clear();
    }
}
