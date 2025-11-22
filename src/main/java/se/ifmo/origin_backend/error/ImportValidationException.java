package se.ifmo.origin_backend.error;

import java.util.List;

public class ImportValidationException extends RuntimeException {

    private final List<RowError> errors;

    public ImportValidationException(List<RowError> errors) {
        super("Import validation failed with " + errors.size() + " error(s)");
        this.errors = errors;
    }

    public List<RowError> getErrors() {
        return errors;
    }
}
