package se.ifmo.origin_backend.controller;

import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.event.AddrEvent;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.service.AddressService;

@RestController
@RequestMapping("/addrs")
@RequiredArgsConstructor
public class AddressController {
    private final ApplicationEventPublisher events;
    private final AddressService service;

    @GetMapping
    public List<Address> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Address getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Address create(@RequestBody @Valid AddressDTO dto) {
        var created = service.create(dto);
        events.publishEvent(new AddrEvent("CREATED", created.getId()));
        return created;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Address update(@PathVariable Long id, @RequestBody @Valid AddressDTO dto) {
        var updated = service.update(id, dto);
        events.publishEvent(new AddrEvent("UPDATED", updated.getId()));
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
        events.publishEvent(new AddrEvent("DELETED", id));
    }
}
