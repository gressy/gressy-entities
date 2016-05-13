package gressy.entities.exceptions;

public class NotLoggedInException extends GressyException {

    public NotLoggedInException() {
        super(401, "You are not logged in.");
    }

    public NotLoggedInException(String message) {
        super(401, message);
    }

}
