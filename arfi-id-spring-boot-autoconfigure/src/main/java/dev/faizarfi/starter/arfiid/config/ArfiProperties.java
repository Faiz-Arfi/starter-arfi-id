package dev.faizarfi.starter.arfiid.config;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "arfi-id")
public class ArfiProperties {

    @NotBlank(message = "Arfi ID issuer URL is mandatory (e.g., https://api.id.faizarfi.dev or http://localhost:8080)")
    private String issuer;

    @NotBlank(message = "Arfi ID client ID is mandatory for multi-tenant identification")
    private String clientId;

    @NotBlank(message = "Arfi ID client secret is mandatory for secure token verification")
    private String clientSecret;

    @NotBlank(message = "Arfi ID redirect URI is mandatory for OAuth callback handling")
    private String redirectUri;

    private CookieProperties cookie = new CookieProperties();

    @Data
    public static class CookieProperties {

        private String prefix = "pf_";
        private String accessToken = "access_token";
        private String refreshToken = "refresh_token";
        private boolean httpOnly = true;
        private boolean secure = false;
        private String path = "/";
        /**
         * Cookie Domain - Null Means Current Host
         */
        private String domain;
        private String sameSite = "Lax";
        /**
         * How Long the cookies can live in the browser after they have been created.
         */
        private int accessCookieMaxAge = 86400; // 1 day
        private int refreshCookieMaxAge = 604800; // 7 days

    }
}