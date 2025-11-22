package se.ifmo.origin_backend.error;

import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.stream.Collectors;

public record RowError(int row, String message) {
    public static <T> RowError fromViolations(int row, Set<ConstraintViolation<T>> violations) {
        String msg = violations.stream()
            .map(v -> v.getPropertyPath() + " " + v.getMessage())
            .collect(Collectors.joining("; "));
        return new RowError(row, msg);
    }
}
