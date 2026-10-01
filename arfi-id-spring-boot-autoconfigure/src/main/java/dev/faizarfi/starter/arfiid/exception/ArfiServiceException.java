package dev.faizarfi.starter.arfiid.exception;

/**
 * Exception thrown when the Arfi ID authentication service cannot be
 * successfully reached or returns an unexpected service-level response.
 *
 * <p>This exception represents failures in communication with the Arfi ID
 * service rather than failures caused by invalid user authentication
 * credentials or tokens.</p>
 *
 * <p>Typical scenarios include:</p>
 * <ul>
 *     <li>Arfi ID service being unavailable</li>
 *     <li>Connection or network failures</li>
 *     <li>Request timeouts</li>
 *     <li>Unexpected responses from Arfi ID</li>
 *     <li>Server-side failures returned by Arfi ID</li>
 * </ul>
 *
 * @see ArfiStarterException
 */
public class ArfiServiceException extends ArfiStarterException {

    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message description of the service failure
     */
    public ArfiServiceException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a descriptive error message and the
     * underlying cause.
     *
     * @param message description of the service failure
     * @param cause the underlying exception that caused the failure
     */
    public ArfiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}