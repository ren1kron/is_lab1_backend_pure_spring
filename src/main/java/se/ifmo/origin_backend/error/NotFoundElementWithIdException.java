package se.ifmo.origin_backend.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundElementWithIdException extends RuntimeException {
    public NotFoundElementWithIdException(String elementName, long id) {
        super(elementName + " with id " + id + " not found");
    }
}
