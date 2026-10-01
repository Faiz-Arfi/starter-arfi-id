package dev.faizarfi.starter.arfiid.exception;

/**
 * Base exception for errors originating from the Arfi ID Spring Boot starter.
 *
 * <p>This exception provides a common parent type for all starter-specific
 * exceptions. It allows consumers of the starter to distinguish errors
 * originating from the Arfi ID infrastructure from exceptions belonging to
 * the consuming application's business logic.</p>
 *
 * <p>Application-specific business exceptions should not extend this class.
 * They should be handled by the consuming application's own exception
 * handling mechanism.</p>
 *
 * @see ArfiAuthenticationException
 * @see ArfiServiceException
 */
public class ArfiStarterException extends RuntimeException {

    /**
     * Creates a starter exception with a descriptive error message.
     *
     * @param message description of the error
     */
    public ArfiStarterException(String message) {
        super(message);
    }

    /**
     * Creates a starter exception with a descriptive error message
     * and the underlying cause.
     *
     * @param message description of the error
     * @param cause the underlying exception that caused this error
     */
    public ArfiStarterException(String message, Throwable cause) {
        super(message, cause);
    }
}