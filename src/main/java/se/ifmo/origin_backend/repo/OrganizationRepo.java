package se.ifmo.origin_backend.repo;

import org.springframework.data.domain.Pageable;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.model.OrganizationType;

import java.util.List;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Integer>, JpaSpecificationExecutor<Organization> {
    Long countAllByPostalAddress_StreetLessThan(String street);

    List<Organization> findAllByPostalAddress_StreetGreaterThan(String postalAddressStreetIsGreaterThan);

    @Query("select distinct o.type from Organization o")
    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "query.organization.distinctTypes")
    })
    List<OrganizationType> findDistinctTypes();

    List<Organization> findAllByOrderByAnnualTurnoverDesc(Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
