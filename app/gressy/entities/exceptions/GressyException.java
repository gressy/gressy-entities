package gressy.entities.exceptions;

/**
 * Lightweight exception for easily classify and return API errors to the client.
 */
public class GressyException extends RuntimeException {

    private int statusCode;

    public GressyException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public GressyException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the status code associated with the exception.
     * @return The status code associated with the exception.
     */
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
