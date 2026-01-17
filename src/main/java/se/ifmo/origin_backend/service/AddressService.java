package se.ifmo.origin_backend.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.error.DuplicateAddressException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.repo.AddressRepo;

@Service
@AllArgsConstructor
public class AddressService {
    private AddressRepo repo;

    @Transactional(readOnly = true)
    public List<Address> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Address getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Address", id));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Address create(AddressDTO dto) {
        if (repo.existsByStreet(dto.street())) {
            throw new DuplicateAddressException(dto.street());
        }

        var addr = new Address(null, dto.street());
        return repo.save(addr);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Address update(Long id, AddressDTO dto) {
        var addr = repo.findById(id)
            .orElseThrow(() -> new NotFoundElementWithIdException("Address", id));
        if (repo.existsByStreetAndIdNot(dto.street(), id)) {
            throw new DuplicateAddressException(dto.street());
        }
        addr.setStreet(dto.street());
        return repo.save(addr);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Address", id);
        repo.deleteById(id);
    }
}
