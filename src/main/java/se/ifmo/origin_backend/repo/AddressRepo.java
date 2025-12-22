package se.ifmo.origin_backend.repo;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Address;

import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    Optional<Address> findByStreet(String street);

    boolean existsByStreet(@Size(max = 63) String street);
}
