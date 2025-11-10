package se.ifmo.origin_backend.controller;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    // Fallback for DB-level unique violations (in case of race)
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex) {
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
//        pd.setTitle("Duplicate coordinates");
//        pd.setDetail("Coordinates with the same (x,y) already exist.");
//        pd.setProperty("code", "COORDINATES_DUPLICATE");
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
//    }

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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleTx(TransactionSystemException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Deletion not allowed");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "FK_CONSTRAINT");


        return ResponseEntity.badRequest().body(pd);
    }
}
