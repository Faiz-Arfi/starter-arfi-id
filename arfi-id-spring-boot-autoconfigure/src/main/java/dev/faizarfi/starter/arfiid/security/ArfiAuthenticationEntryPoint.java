package dev.faizarfi.starter.arfiid.security;

import dev.faizarfi.starter.arfiid.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Handles requests that require authentication but do not have an
 * authenticated principal.
 *
 * <p>This component is part of the reusable security infrastructure provided
 * by the Arfi ID Spring Boot starter. It converts Spring Security's
 * authentication failure into the starter's standard {@link ApiResponse}
 * format.</p>
 *
 * <p>This handler is responsible only for authentication failures.
 * Authorization failures, such as an authenticated user attempting to access
 * an endpoint requiring a different role, are handled separately by an
 * access-denied handler.</p>
 *
 * <p>HTTP status mapping:</p>
 * <ul>
 *     <li>{@code 401 UNAUTHORIZED} → {@code AUTHENTICATION_REQUIRED}</li>
 * </ul>
 */
@RequiredArgsConstructor
public class ArfiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Handles an unauthenticated request.
     *
     * @param request the HTTP request that requires authentication
     * @param response the HTTP response to be returned
     * @param authException the Spring Security authentication exception
     * @throws IOException if the response cannot be written
     */
    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException {

        int status = HttpServletResponse.SC_UNAUTHORIZED;

        ApiResponse<Void> apiResponse = ApiResponse.fail(
                null,
                "AUTHENTICATION_REQUIRED",
                request.getRequestURI(),
                status
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}