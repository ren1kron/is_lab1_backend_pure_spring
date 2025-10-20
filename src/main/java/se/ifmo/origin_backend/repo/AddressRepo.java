package se.ifmo.origin_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Address;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {}
