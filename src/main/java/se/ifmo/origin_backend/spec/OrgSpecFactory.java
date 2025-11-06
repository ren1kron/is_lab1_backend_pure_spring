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
            .and(coordXBetween(req.coordXMin(), req.coordXMax()))
            .and(coordYBetween(req.coordYMin(), req.coordYMax()))
            .and(locationXBetween(req.locationXMin(), req.locationXMax()))
            .and(locationYBetween(req.locationYMin(), req.locationYMax()))
            .and(locationZBetween(req.locationZMin(), req.locationZMax()))
            .and(loactionNameContains(req.name()));
    }
}
