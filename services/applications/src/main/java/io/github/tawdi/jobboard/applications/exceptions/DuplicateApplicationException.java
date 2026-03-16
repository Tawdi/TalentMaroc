package io.github.tawdi.jobboard.applications.exceptions;

public class DuplicateApplicationException extends RuntimeException {

    public DuplicateApplicationException(String message) {
        super(message);
    }
}
