package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.error.DuplicateLocationException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.repo.LocationRepo;

@Service
@AllArgsConstructor
public class LocationService {
    private final LocationRepo repo;

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Location getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Location", id));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location create(LocationDTO dto) {
        if (repo.existsByXAndYAndZAndName(dto.x(), dto.y(), dto.z(), dto.name())) {
            throw new DuplicateLocationException(dto);
        }
        var loc = new Location(null, dto.x(), dto.y(), dto.z(), dto.name());
        return repo.save(loc);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location update(Long id, LocationDTO dto) {
        var loc = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Location", id));

        if (repo.existsByXAndYAndZAndNameAndIdNot(dto.x(), dto.y(), dto.z(), dto.name(), id)) {
            throw new DuplicateLocationException(dto);
        }
        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        return repo.save(loc);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Location", id);
        repo.deleteById(id);
        System.out.println("DELETED SUCK SEX FULLY!!!");
    }
}
