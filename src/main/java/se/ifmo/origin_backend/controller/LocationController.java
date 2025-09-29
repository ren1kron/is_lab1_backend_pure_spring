package se.ifmo.origin_backend.controller;


import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/locs")
public class LocationController {

    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return service.getLocations();
    }

    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable Long id) {
        return service.getLocationById(id);
    }

    @PostMapping
    public void addLocation(@RequestBody LocationDTO dto) {
        service.addLocation(dto);
    }

    @PutMapping("/{id}")
    public void updateLocation(@PathVariable Long id, @RequestBody LocationDTO dto) {
        service.updateLocation(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable Long id) {
        service.deleteLocation(id);
    }
}
