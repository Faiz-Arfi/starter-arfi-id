package dev.faizarfi.starter.arfiid.security;

import dev.faizarfi.starter.arfiid.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Handles authorization failures where an authenticated user does not have
 * sufficient permissions to access a protected resource.
 *
 * <p>This component is part of the reusable security infrastructure provided
 * by the Arfi ID Spring Boot starter. It converts Spring Security's
 * authorization failure into the starter's standard {@link ApiResponse}
 * format.</p>
 *
 * <p>This handler is invoked after authentication has already succeeded.
 * For example, an authenticated user with {@code ROLE_USER} attempting to
 * access an endpoint that requires {@code ROLE_ADMIN} results in a
 * {@code 403 FORBIDDEN} response.</p>
 *
 * <p>HTTP status mapping:</p>
 * <ul>
 *     <li>{@code 403 FORBIDDEN} → {@code ACCESS_DENIED}</li>
 * </ul>
 */
@RequiredArgsConstructor
public class ArfiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Handles an authenticated user attempting to access a resource for which
     * they do not have sufficient permissions.
     *
     * @param request the HTTP request that was denied
     * @param response the HTTP response to be returned
     * @param accessDeniedException the Spring Security access-denied exception
     * @throws IOException if the response cannot be written
     */
    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {

        int status = HttpServletResponse.SC_FORBIDDEN;

        ApiResponse<Void> apiResponse = ApiResponse.fail(
                null,
                "ACCESS_DENIED",
                request.getRequestURI(),
                status
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}