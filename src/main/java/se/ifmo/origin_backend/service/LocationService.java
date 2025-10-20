package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.repo.LocationRepo;

@Service
@AllArgsConstructor
public class LocationService {
    private final ApplicationEventPublisher events;
    private final LocationRepo repo;

    public record LocEvent(
        String type,
        long id) {}

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Location getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Location",
                id));
    }

    @Transactional
    public Location create(LocationDTO dto) {
        var loc = new Location();
        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        var saved = repo.save(loc);
        events.publishEvent(new LocEvent("CREATED", saved.getId()));
        return saved;
    }

    @Transactional
    public Location update(Long id, LocationDTO dto) {
        var loc = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Location",
                id));

        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        var saved = repo.save(loc);
        events.publishEvent(new LocEvent("UPDATED", saved.getId()));
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Location", id);
        repo.deleteById(id);
        events.publishEvent(new LocEvent("DELETED", id));
    }
}


@Component
@AllArgsConstructor
class LocEventForwarder {
    private final SimpMessagingTemplate broker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LocationService.LocEvent e) {
        broker.convertAndSend("/topic/loc-changed", e);
    }
}
