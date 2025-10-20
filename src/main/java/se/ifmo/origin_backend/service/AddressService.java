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
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.error.DuplicateAddressException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.repo.AddressRepo;

@Service
@AllArgsConstructor
public class AddressService {
    private final ApplicationEventPublisher events;
    private AddressRepo repo;

    public record AddrEvent(
        String type,
        long id) {}

    @Transactional(readOnly = true)
    public List<Address> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Address getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Address",
                id));
    }

    @Transactional
    public Address create(AddressDTO dto) {
        var addr = new Address();
        addr.setStreet(dto.street());
        try {
            var saved = repo.saveAndFlush(addr);
            events.publishEvent(new AddrEvent("CREATED", saved.getId()));
            return saved;
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateAddressException(dto.street());
        }
    }

    @Transactional
    public Address update(Long id, AddressDTO dto) {
        var addr = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException(
                "Address",
                id));
        addr.setStreet(dto.street());
        try {
            var saved = repo.saveAndFlush(addr);
            events.publishEvent(new AddrEvent("UPDATED", saved.getId()));
            return saved;
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateAddressException(dto.street());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Address", id);
        repo.deleteById(id);
        events.publishEvent(new AddrEvent("DELETED", id));
    }
}


@Component
@AllArgsConstructor
class AddrEventForwarder {
    private final SimpMessagingTemplate broker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(AddressService.AddrEvent e) {
        broker.convertAndSend("/topic/addr-changed", e);
    }
}
