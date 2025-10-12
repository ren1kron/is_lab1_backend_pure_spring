package se.ifmo.origin_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.error.DuplicateAddressException;
import se.ifmo.origin_backend.error.DuplicateCoordinatesException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.repo.AddressRepo;

import java.util.List;

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

    @Transactional
    public Address create(AddressDTO dto) {
        var addr = new Address();
        addr.setStreet(dto.street());
        try {
            return repo.saveAndFlush(addr);
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateAddressException(dto.street());
        }
    }

    @Transactional
    public Address update(Long id, AddressDTO dto) {
        var addr = repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Address", id));
        addr.setStreet(dto.street());
        try {
            return repo.saveAndFlush(addr);
        } catch (org.springframework.orm.jpa.JpaSystemException ex) {
            throw new DuplicateAddressException(dto.street());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty()) throw new NotFoundElementWithIdException("Address", id);
        repo.deleteById(id);
    }
}
