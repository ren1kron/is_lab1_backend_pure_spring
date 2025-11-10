package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.error.DuplicateAddressException;
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

    @Transactional
    public Location create(LocationDTO dto) {
        var loc = new Location(null, dto.x(), dto.y(), dto.z(), dto.name());
        return repo.save(loc);
    }

    @Transactional
    public Location update(Long id, LocationDTO dto) {
        var loc = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Location", id));

        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        return repo.save(loc);
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Location", id);
        repo.deleteById(id);
        System.out.println("DELETED SUCK SEX FULLY!!!");
    }
}
