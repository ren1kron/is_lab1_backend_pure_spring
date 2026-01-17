package se.ifmo.origin_backend.repo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    Optional<Location> findByXAndYAndZAndName(float x, int y, float z, @NotNull @Size(max = 63) String name);

    boolean existsByXAndYAndZAndName(float x, int y, float z, @NotNull @Size(max = 63) String name);

    boolean existsByXAndYAndZAndNameAndIdNot(
        float x,
        int y,
        float z,
        @NotNull @Size(max = 63) String name,
        Long id);
}
