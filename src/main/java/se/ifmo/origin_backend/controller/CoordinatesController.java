package se.ifmo.origin_backend.controller;

import jakarta.validation.Valid;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.event.CordEvent;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.service.CoordinatesService;

@RestController
@RequestMapping("/coords")
@AllArgsConstructor
public class CoordinatesController {
    private final ApplicationEventPublisher events;
    private final CoordinatesService service;

    @GetMapping
    public List<Coordinates> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Coordinates getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coordinates create(@Valid @RequestBody CoordinatesDTO dto) {
        var created = service.create(dto);
        events.publishEvent(new CordEvent("CREATED", created.getId()));
        return created;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Coordinates update(@PathVariable long id, @RequestBody @Valid CoordinatesDTO dto) {
        var updated = service.update(id, dto);
        events.publishEvent(new CordEvent("UPDATED", updated.getId()));
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
        events.publishEvent(new CordEvent("DELETED", id));
    }
}
