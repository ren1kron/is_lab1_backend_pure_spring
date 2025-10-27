package se.ifmo.origin_backend.controller;

import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.event.LocEvent;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.service.LocationService;

@RestController
@RequestMapping("/locs")
@RequiredArgsConstructor
public class LocationController {
    private final ApplicationEventPublisher events;
    private final LocationService service;

    @GetMapping
    public List<Location> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Location getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Location create(@RequestBody @Valid LocationDTO dto) {
        var created = service.create(dto);
        events.publishEvent(new LocEvent("CREATED", created.getId()));
        return created;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Location update(@PathVariable Long id, @Valid @RequestBody LocationDTO dto) {
        var updated = service.update(id, dto);
        events.publishEvent(new LocEvent("UPDATED", updated.getId()));
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
        events.publishEvent(new LocEvent("DELETED", id));
    }
}
