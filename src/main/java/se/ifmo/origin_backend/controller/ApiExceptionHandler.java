package se.ifmo.origin_backend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.ifmo.origin_backend.error.DuplicateException;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundElementWithIdException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundElementWithIdException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "not_found", "message", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Duplicate");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "ENTITY_DUPLICATE");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    // Validation errors (@NotNull, @Max, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
        org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        pd.setProperty("code", "VALIDATION_ERROR");
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleDBValidation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "VALIDATION_ERROR");
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ProblemDetail> handleTx(TransactionSystemException ex) {
        if (hasCause(ex, OptimisticLockException.class)
            || hasCause(ex, ObjectOptimisticLockingFailureException.class)) {
            return buildProblem(
                HttpStatus.CONFLICT,
                "Concurrent modification",
                "Organization was updated or deleted by another transaction.",
                "CONCURRENT_MODIFICATION");
        }
        if (hasCause(ex, EntityNotFoundException.class)) {
            return buildProblem(
                HttpStatus.NOT_FOUND,
                "Not found",
                "Organization no longer exists.",
                "NOT_FOUND");
        }
        if (hasCause(ex, SQLIntegrityConstraintViolationException.class)) {
            return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Constraint violation",
                "Operation violates a database constraint.",
                "DB_CONSTRAINT");
        }
        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Transaction failed",
            "Could not commit transaction.",
            "TX_FAILED");
    }

    private static ResponseEntity<ProblemDetail> buildProblem(
        HttpStatus status,
        String title,
        String detail,
        String code) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("code", code);
        return ResponseEntity.status(status).body(pd);
    }

    private static boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (type.isInstance(t)) {
                return true;
            }
        }
        return false;
    }
}
