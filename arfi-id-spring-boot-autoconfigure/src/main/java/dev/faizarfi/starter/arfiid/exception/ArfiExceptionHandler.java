package dev.faizarfi.starter.arfiid.exception;

import dev.faizarfi.starter.arfiid.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for exceptions originating from the Arfi ID
 * Spring Boot starter.
 *
 * <p>This handler is responsible only for exceptions owned by the starter.
 * It does not handle business exceptions belonging to applications that
 * consume the starter.</p>
 *
 * <p>The handler converts starter-specific exceptions into the standard
 * {@link ApiResponse} format provided by the starter.</p>
 *
 * <h2>Exception-to-HTTP-status mapping</h2>
 *
 * <ul>
 *     <li>
 *         {@link ArfiAuthenticationException} →
 *         {@code 401 UNAUTHORIZED} —
 *         authentication or token validation failed.
 *     </li>
 *     <li>
 *         {@link ArfiServiceException} →
 *         {@code 502 BAD_GATEWAY} —
 *         communication with Arfi ID failed.
 *     </li>
 *     <li>
 *         {@link ArfiStarterException} →
 *         {@code 500 INTERNAL_SERVER_ERROR} —
 *         an unexpected starter-level failure occurred.
 *     </li>
 * </ul>
 *
 * <p>Business exceptions from the consuming application should be handled
 * by the consuming application's own exception handler.</p>
 */
@RestControllerAdvice
public class ArfiExceptionHandler {

    /**
     * Handles authentication failures originating from the Arfi starter.
     *
     * @param exception the authentication exception
     * @param request the HTTP request that caused the exception
     * @return a standardized unauthorized API response
     */
    @ExceptionHandler(ArfiAuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            ArfiAuthenticationException exception,
            HttpServletRequest request
    ) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiResponse<Void> response = ApiResponse.fail(
                null,
                exception.getMessage(),
                request.getRequestURI(),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    /**
     * Handles failures communicating with the Arfi ID service.
     *
     * @param exception the Arfi service exception
     * @param request the HTTP request that caused the exception
     * @return a standardized bad-gateway API response
     */
    @ExceptionHandler(ArfiServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(
            ArfiServiceException exception,
            HttpServletRequest request
    ) {

        HttpStatus status = HttpStatus.BAD_GATEWAY;

        ApiResponse<Void> response = ApiResponse.fail(
                null,
                exception.getMessage(),
                request.getRequestURI(),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    /**
     * Handles unexpected exceptions originating from the Arfi starter.
     *
     * <p>This method acts as a fallback for starter-specific exceptions
     * that do not have a more specific handler.</p>
     *
     * @param exception the starter exception
     * @param request the HTTP request that caused the exception
     * @return a standardized internal-server-error API response
     */
    @ExceptionHandler(ArfiStarterException.class)
    public ResponseEntity<ApiResponse<Void>> handleStarterException(
            ArfiStarterException exception,
            HttpServletRequest request
    ) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiResponse<Void> response = ApiResponse.fail(
                null,
                exception.getMessage(),
                request.getRequestURI(),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}