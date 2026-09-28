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

    private String cookiePrefix = "km_";
}