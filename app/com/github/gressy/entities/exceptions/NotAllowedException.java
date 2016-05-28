package com.github.gressy.entities.exceptions;

public class NotAllowedException extends GressyException {

    public NotAllowedException() {
        super(403, "Access forbidden.");
    }

    public NotAllowedException(String message) {
        super(403, message);
    }

}
