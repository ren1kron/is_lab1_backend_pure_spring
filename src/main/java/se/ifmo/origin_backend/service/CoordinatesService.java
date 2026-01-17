package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.error.DuplicateCoordinatesException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.repo.CoordinatesRepo;

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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Coordinates create(CoordinatesDTO dto) {
        if (repo.existsByXAndY(dto.x(), dto.y())) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
        var cords = new Coordinates(null, dto.x(), dto.y());
        return repo.save(cords);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Coordinates update(long id, CoordinatesDTO dto) {
        var cords = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", id));

        if (repo.existsByXAndYAndIdNot(dto.x(), dto.y(), id)) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
        cords.setX(dto.x());
        cords.setY(dto.y());
        return repo.save(cords);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Coordinates", id);
        repo.deleteById(id);
    }
}
