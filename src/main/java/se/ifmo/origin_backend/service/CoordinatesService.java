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
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.error.DuplicateCoordinatesException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.repo.CoordinatesRepo;

@Service
@AllArgsConstructor
public class CoordinatesService {
    private final ApplicationEventPublisher events;
    private final CoordinatesRepo repo;

    public record CordEvent(
        String type,
        long id) {}

    @Transactional(readOnly = true)
    public List<Coordinates> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Coordinates getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Coordinates",
                id));
    }

    @Transactional
    public Coordinates create(CoordinatesDTO dto) {
        var cords = new Coordinates();
        cords.setX(dto.x());
        cords.setY(dto.y());
        try {
            var saved = repo.saveAndFlush(cords);
            events.publishEvent(new CordEvent("CREATED", saved.getId()));
            return saved;
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
    }

    @Transactional
    public Coordinates update(long id, CoordinatesDTO dto) {
        var cords = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Coordinates",
                id));

        cords.setX(dto.x());
        cords.setY(dto.y());
        try {
            var saved = repo.saveAndFlush(cords);
            events.publishEvent(new CordEvent("UPDATED", saved.getId()));
            return saved;
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateCoordinatesException(dto.x(), dto.y());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Coordinates", id);
        repo.deleteById(id);
        events.publishEvent(new CordEvent("DELETED", id));
    }
}


@Component
@AllArgsConstructor
class CordEventForwarder {
    private final SimpMessagingTemplate broker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CoordinatesService.CordEvent e) {
        broker.convertAndSend("/topic/cord-changed", e);
    }
}
