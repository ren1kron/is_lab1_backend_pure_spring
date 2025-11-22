package se.ifmo.origin_backend.repo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    Optional<Location> findByXAndYAndZAndName(float x, int y, float z, @NotNull @Size(max = 63) String name);
}
