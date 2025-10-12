package se.ifmo.origin_backend.error;

public class DuplicateAddressException extends DuplicateException {
    public DuplicateAddressException(String street) {
        super("Address already exist: (" + street + ")");
    }
}
