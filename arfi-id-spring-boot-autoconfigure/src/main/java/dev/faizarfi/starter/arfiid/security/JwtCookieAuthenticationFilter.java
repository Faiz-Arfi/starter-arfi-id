package dev.faizarfi.starter.arfiid.security;

import dev.faizarfi.starter.arfiid.config.ArfiProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final ArfiProperties properties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractAccessTokenFromCookie(request);

        if (token != null) {
            try {
                // Decode the secret key from BASE64 (same way Arfi ID signs tokens)
                byte[] decodedKey = Decoders.BASE64.decode(properties.getClientSecret());
                SecretKey secretKey = Keys.hmacShaKeyFor(decodedKey);

                // Parse and verify the JWT token
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String authority = role != null
                        ? (role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        : "ROLE_USER";

                log.debug("Extracted claims from JWT: email={}, role={}", email, role);
                log.debug("Changed the authority to {}", authority);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var grantedAuthority = new SimpleGrantedAuthority(authority);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            email, null, Collections.singletonList(grantedAuthority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Successfully authenticated user {} via namespaced cookie", email);
                }

            } catch (ExpiredJwtException e) {

                log.debug("Access token expired");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "ACCESS_TOKEN_EXPIRED");
                return;

            } catch (Exception e) {

                log.warn("Invalid JWT token {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "INVALID_ACCESS_TOKEN");
                return;

            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        String targetCookieName = properties.getCookiePrefix() + "accessToken";

        return Arrays.stream(cookies)
                .filter(cookie -> targetCookieName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}