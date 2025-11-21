package se.ifmo.origin_backend.error;

import se.ifmo.origin_backend.dto.LocationDTO;

public class DuplicateLocationException extends DuplicateException {
    public DuplicateLocationException(LocationDTO loc) {
        super(String.format("Location already exists: (%s)", loc.fieldsToString()));
    }
}
