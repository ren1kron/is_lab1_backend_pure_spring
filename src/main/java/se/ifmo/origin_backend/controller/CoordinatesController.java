package se.ifmo.origin_backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.service.CoordinatesService;

@RestController
@RequestMapping("/coords")
public class CoordinatesController {
    // TODO 1. переименовать add... в create (без названия, что сreate. и так
    // понятно по
    // контроллеру-сервису)
    // 2. Сделать так, чтобы post/put запросы возвращали что-то, что может понять
    // успешность создания
    // объекта (сам объект, либо его id, либо путь до него).
    // лучшее решение – возвращать фул объект. Мы получаем отвалидированные сервером
    // данные,
    // которые показываем пользователю
    // 3.

    private final CoordinatesService service;

    public CoordinatesController(CoordinatesService service) {
        this.service = service;
    }

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
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Coordinates update(@PathVariable long id, @RequestBody @Valid CoordinatesDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
