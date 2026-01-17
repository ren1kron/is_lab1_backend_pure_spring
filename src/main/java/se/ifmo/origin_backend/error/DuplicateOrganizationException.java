package se.ifmo.origin_backend.error;

public class DuplicateOrganizationException extends DuplicateException {
    public DuplicateOrganizationException(String name) {
        super("Organization already exists: (" + name + ")");
    }
}
