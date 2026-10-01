package dev.faizarfi.starter.arfiid.exception;

/**
 * Exception thrown when an authentication-related operation fails.
 *
 * <p>This exception represents failures where the authentication credentials,
 * authorization code, access token, refresh token, or JWT cannot be
 * successfully validated or processed.</p>
 *
 * <p>Typical scenarios include:</p>
 * <ul>
 *     <li>Invalid or expired JWT access token</li>
 *     <li>Invalid or expired refresh token</li>
 *     <li>Invalid authorization code</li>
 *     <li>Authentication credentials being rejected by Arfi ID</li>
 * </ul>
 *
 * <p>These failures are expected to be translated into an appropriate
 * authentication-related HTTP response by the starter's exception handling
 * mechanism.</p>
 *
 * @see ArfiStarterException
 */
public class ArfiAuthenticationException extends ArfiStarterException {

    /**
     * Creates an authentication exception with a descriptive error message.
     *
     * @param message description of the authentication failure
     */
    public ArfiAuthenticationException(String message) {
        super(message);
    }

    /**
     * Creates an authentication exception with a descriptive error message
     * and the underlying cause.
     *
     * @param message description of the authentication failure
     * @param cause the underlying exception that caused this failure
     */
    public ArfiAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}