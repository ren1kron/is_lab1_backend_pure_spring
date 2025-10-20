package se.ifmo.origin_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Coordinates;

@Repository
public interface CoordinatesRepo extends JpaRepository<Coordinates, Long> {}
