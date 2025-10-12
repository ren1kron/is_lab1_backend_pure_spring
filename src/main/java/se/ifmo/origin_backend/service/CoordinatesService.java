package se.ifmo.origin_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.error.DuplicateCoordinatesException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.repo.CoordinatesRepo;

import java.util.List;

@Service
@AllArgsConstructor
public class CoordinatesService {

    private final CoordinatesRepo repo;

    @Transactional(readOnly = true)
    public List<Coordinates> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Coordinates getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", id));
    }

    @Transactional
    public Coordinates create(CoordinatesDTO dto) {
        var cords = new Coordinates();
        cords.setX(dto.x());
        cords.setY(dto.y());
        try {
            return repo.saveAndFlush(cords);
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
    }

    @Transactional
    public Coordinates update(long id, CoordinatesDTO dto) {
        var cords = repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", id));

        cords.setX(dto.x());
        cords.setY(dto.y());
        try {
            return repo.saveAndFlush(cords);
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty()) throw new NotFoundElementWithIdException("Coordinates", id);
        repo.deleteById(id);
    }
}
