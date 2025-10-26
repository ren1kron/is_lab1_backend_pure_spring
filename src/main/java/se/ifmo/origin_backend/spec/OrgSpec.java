package se.ifmo.origin_backend.spec;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import se.ifmo.origin_backend.model.Organization;
import se.ifmo.origin_backend.model.OrganizationType;

import java.time.LocalDate;
import java.util.List;

public final class OrgSpec {
    private static String escapeLike(String s) {
        // escape % and _ with backslash for SQL LIKE
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    // coords
    public static Specification<Organization> coordXBetween(Long min, Long max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Long> path = root.join("coordinates").get("x");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    public static Specification<Organization> coordYBetween(Long min, Long max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Long> path = root.join("coordinates").get("y");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    // location
    public static Specification<Organization> locationXBetween(Float min, Float max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Float> path = root.join("officialAddress").get("x");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    public static Specification<Organization> locationYBetween(Integer min, Integer max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Integer> path = root.join("officialAddress").get("y");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    public static Specification<Organization> locationZBetween(Float min, Float max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Float> path = root.join("officialAddress").get("z");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    // strings
    public static Specification<Organization> loactionNameContains(String q) {
        return (root, cq, cb) -> (q == null || q.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.join("location").get("name")), "%" + escapeLike(q.toLowerCase()) + "%", '\\');
    }

    // ids
    public static Specification<Organization> hasId(Integer providedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), providedId);
    }

    public static Specification<Organization> hasCoordinatesId(Long providedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("coordinates").get("id"), providedId);
    }

    public static Specification<Organization> hasOfficialAddressId(Long providedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("officialAddress").get("id"), providedId);
    }

    public static Specification<Organization> hasPostalAddressId(Long providedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("postalAddress").get("id"), providedId);
    }

    // nums
    public static Specification<Organization> hasAnnualTurnover(Long providedNum) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("annualTurnover"), providedNum);
    }

    public static Specification<Organization> annualTurnoverBetween(Long min, Long max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Long> path = root.get("annualTurnover");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    public static Specification<Organization> hasEmployeesCount(Long providedNum) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employeesCount"), providedNum);
    }

    public static Specification<Organization> employeesBetween(Long min, Long max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Long> path = root.get("employeesCount");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    public static Specification<Organization> hasRating(Integer providedNum) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("rating"), providedNum);
    }

    public static Specification<Organization> ratingBetween(Integer min, Integer max) {
        return (root, cq, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();
            Path<Integer> path = root.get("rating");
            if (min != null && max != null)
                return cb.between(path, min, max);
            return (min != null) ? cb.ge(path, min)
                : cb.le(path, max);
        };
    }

    // strings
    public static Specification<Organization> nameContains(String q) {
        return (root, cq, cb) -> (q == null || q.isBlank())
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("name")), "%" + escapeLike(q.toLowerCase()) + "%", '\\');
    }

    // date
    public static Specification<Organization> createdBetween(LocalDate from, LocalDate to) {
        return (root, cq, cb) -> {
            if (from == null && to == null)
                return cb.conjunction();
            Path<LocalDate> path = root.get("creationDate");
            if (from != null && to != null)
                return cb.between(path, from, to);
            return (from != null) ? cb.greaterThanOrEqualTo(path, from)
                : cb.lessThanOrEqualTo(path, to);
        };
    }

    // enum
    public static Specification<Organization> typeIn(List<OrganizationType> types) {
        return (root, cq, cb) -> (types == null || types.isEmpty())
            ? cb.conjunction()
            : root.get("type").in(types);
    }
}
