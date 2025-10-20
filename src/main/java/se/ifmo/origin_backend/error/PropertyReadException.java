package se.ifmo.origin_backend.error;

public class PropertyReadException extends RuntimeException {
    public PropertyReadException(String s, Exception e) {
        super(s, e);
    }
}
