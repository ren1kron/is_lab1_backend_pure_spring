package se.ifmo.origin_backend.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.dto.OrgSearchRequestDTO;
import se.ifmo.origin_backend.model.Organization;

import static se.ifmo.origin_backend.spec.OrgSpec.*;

@Component
public class OrgSpecFactory {
    public Specification<Organization> from(OrgSearchRequestDTO req) {
        return typeIn(req.types())
            .and(createdBetween(req.createdFrom(), req.createdTo()))
            .and(nameContains(req.name()))
            .and(ratingBetween(req.ratingMin(), req.ratingMax()))
            .and(annualTurnoverBetween(req.annualTurnoverMin(), req.annualTurnoverMax()))
            .and(employeesBetween(req.employeesMin(), req.employeesMax()))
            .and(cordXBetween(req.cordXMin(), req.cordXMax()))
            .and(cordYBetween(req.cordYMin(), req.cordYMax()))
            .and(locationXBetween(req.locationXMin(), req.locationXMax()))
            .and(locationYBetween(req.locationYMin(), req.locationYMax()))
            .and(locationZBetween(req.locationZMin(), req.locationZMax()))
            .and(locationNameContains(req.name()));
    }
}
