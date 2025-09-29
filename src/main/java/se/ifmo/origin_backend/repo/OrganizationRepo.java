package se.ifmo.origin_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.dto.OrgFullDTO;
import se.ifmo.origin_backend.model.Organization;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Integer> {
    @Query("""
    select new se.ifmo.origin_backend.dto.OrgFullDTO(
        o.id,
        o.name,
        c.x, c.y,
        o.creationDate,
        l.x, l.y, l.z, l.name,
        o.annualTurnover,
        o.employeesCount,
        o.rating,
        o.type,
        a.street
        )
    from Organization o
    join o.coordinates c
    join o.officialAddress l
    join o.postalAddress a
    """)
    List<OrgFullDTO> findAllFull();

    @Query("""
    select new se.ifmo.origin_backend.dto.OrgFullDTO(
        o.id,
        o.name,
        c.x, c.y,
        o.creationDate,
        l.x, l.y, l.z, l.name,
        o.annualTurnover,
        o.employeesCount,
        o.rating,
        o.type,
        a.street
        )
    from Organization o
    join o.coordinates c
    join o.officialAddress l
    join o.postalAddress a
    where o.id = :id
    """)
    Optional<OrgFullDTO> findFullById(@Param("id") Integer id);
}
