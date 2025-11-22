package se.ifmo.origin_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Coordinates;

import java.util.Optional;

@Repository
public interface CoordinatesRepo extends JpaRepository<Coordinates, Long>, JpaSpecificationExecutor<Coordinates> {
    Optional<Coordinates> findCoordinatesByXAndY(Long x, Long y);
}
