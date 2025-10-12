package se.ifmo.origin_backend.error;

public class DuplicateCoordinatesException extends DuplicateException {
    public DuplicateCoordinatesException(Long x, Long y) {
        super("Coordinates already exist: (" + x + ", " + y + ")");
    }
}
