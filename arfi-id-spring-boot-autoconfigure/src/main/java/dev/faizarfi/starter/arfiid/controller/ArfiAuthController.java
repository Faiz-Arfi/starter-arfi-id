package dev.faizarfi.starter.arfiid.controller;

import dev.faizarfi.starter.arfiid.config.ArfiProperties;
import dev.faizarfi.starter.arfiid.dto.ApiResponse;
import dev.faizarfi.starter.arfiid.dto.AuthResponse;
import dev.faizarfi.starter.arfiid.dto.ExchangeCodeRequest;
import dev.faizarfi.starter.arfiid.dto.UserDto;
import dev.faizarfi.starter.arfiid.service.ArfiAuthServiceClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/arfiid")
@RequiredArgsConstructor
public class ArfiAuthController {

    private final ArfiAuthServiceClient authServiceClient;
    private final ArfiProperties properties;

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(
            @RequestBody @Valid ExchangeCodeRequest request,
            HttpServletResponse response
    ) {
        log.debug("Received callback token exchange request from frontend");

        // Perform server-to-server token exchange with Arfi ID
        AuthResponse arfiTokenResponse = authServiceClient.exchangeCodeWithArfiId(request);

        String accessToken = arfiTokenResponse.getAccessToken();
        String refreshToken = arfiTokenResponse.getRefreshToken();

        // Set App A's HttpOnly Cookies for local session management
        ResponseCookie accessCookie = generateCookie(CookieType.ACCESS_COOKIE,  accessToken);
        ResponseCookie refreshCookie = generateCookie(CookieType.REFRESH_COOKIE,  refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        log.debug("Successfully established local session cookies for App A user");

        // Return user metadata to the frontend (excluding raw tokens for security)
        return ResponseEntity.ok(ApiResponse.success(arfiTokenResponse.getEmail(), "Authentication successful", "/arfiid/callback", HttpStatus.OK.value()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> handleRefresh(HttpServletRequest request) {

        String refreshToken = extractCookieValue(request, CookieType.REFRESH_COOKIE);

        if (refreshToken == null) {
            log.debug("Refresh token not found in cookies for request to /arfiid/refresh");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(null, "Refresh Token not found", "/arfiid/refresh", HttpStatus.UNAUTHORIZED.value()));
        }

        AuthResponse arfiTokenResponse = authServiceClient.refreshAccessToken(refreshToken);
        ResponseCookie accessCookie = generateCookie(CookieType.ACCESS_COOKIE,  arfiTokenResponse.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(null, "Unauthorized", "/arfiid/me", HttpStatus.UNAUTHORIZED.value()));
        }

        String email = authentication.getName();
        String authority = authentication.getAuthorities().
        stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // remove the ROLE_ Prefix that is provided by Spring security context
        String role = authority.startsWith("ROLE_")
                ? authority.substring(5)
                : authority;

        UserDto user = UserDto.builder()
                .role(role)
                .email(email)
                .authenticated(true)
                .build();

        return ResponseEntity.ok(ApiResponse.success(user, "User authenticated", "/arfiid/me", HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOutUser(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = extractCookieValue(request, CookieType.REFRESH_COOKIE);
        if (refreshToken != null) {
            try {
                authServiceClient.revokeSessionAtArfiId(refreshToken);
            } catch (Exception e) {
                log.warn("Failed to notify Arfi ID during logout: {}", e.getMessage());
            }
        }

        ResponseCookie clearAccess = generateCookie(CookieType.CLEAR_ACCESS_COOKIE, "");
        ResponseCookie clearRefresh = generateCookie(CookieType.CLEAR_REFRESH_COOKIE, "");

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful", "/arfiid/logout", HttpStatus.OK.value()));
    }

    private String extractCookieValue(HttpServletRequest request, CookieType type) {

        Cookie[] cookies = request.getCookies();

        String cookieName = switch (type) {
            case ACCESS_COOKIE, CLEAR_ACCESS_COOKIE -> properties.getCookie().getPrefix() + properties.getCookie().getAccessToken();
            case REFRESH_COOKIE, CLEAR_REFRESH_COOKIE -> properties.getCookie().getPrefix() + properties.getCookie().getRefreshToken();
        };

        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> cookieName.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private ResponseCookie generateCookie(CookieType type, String value) {

        String cookieName = switch (type) {
            case ACCESS_COOKIE, CLEAR_ACCESS_COOKIE -> properties.getCookie().getPrefix() + properties.getCookie().getAccessToken();
            case REFRESH_COOKIE, CLEAR_REFRESH_COOKIE -> properties.getCookie().getPrefix() + properties.getCookie().getRefreshToken();
        };

        int maxAge = switch (type) {
            case ACCESS_COOKIE -> properties.getCookie().getAccessCookieMaxAge();
            case REFRESH_COOKIE -> properties.getCookie().getRefreshCookieMaxAge();
            default -> 0;
        };


        return ResponseCookie.from(cookieName, value)
                .httpOnly(properties.getCookie().isHttpOnly())
                .secure(properties.getCookie().isSecure())
                .path(properties.getCookie().getPath())
                .domain(properties.getCookie().getDomain())
                .maxAge(maxAge)
                .sameSite(properties.getCookie().getSameSite())
                .build();
    }

    enum CookieType {
        ACCESS_COOKIE, REFRESH_COOKIE, CLEAR_ACCESS_COOKIE, CLEAR_REFRESH_COOKIE
    }

}