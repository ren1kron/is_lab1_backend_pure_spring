package se.ifmo.origin_backend.controller;

import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.service.CoordinatesService;

import java.util.List;

@RestController
@RequestMapping("/coords")
public class CoordinatesController {

    private final CoordinatesService service;

    public CoordinatesController(CoordinatesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Coordinates> getAllCoordinates() {
        return service.getAllCoordinates();
    }

    @GetMapping("/{id}")
    public Coordinates getCoordinatesById(@PathVariable long id) {
        return service.getCoordinatesById(id);
    }

    @PostMapping
    public void addCoordinates(@RequestBody CoordinatesDTO dto) {
        service.addCoordinates(dto);
    }

    @PutMapping("/{id}")
    public void updateCoordinates(@PathVariable long id, @RequestBody CoordinatesDTO dto) {
        service.updateCoords(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCoordinates(@PathVariable Long id) {
        service.deleteCoordinates(id);
    }
}
