package JobApplicationTracker.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested entity does not exist (or is not visible to the
 * current user). Mapped to HTTP 404 so callers get a clean Not Found response
 * instead of a 500 with a stack trace.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
