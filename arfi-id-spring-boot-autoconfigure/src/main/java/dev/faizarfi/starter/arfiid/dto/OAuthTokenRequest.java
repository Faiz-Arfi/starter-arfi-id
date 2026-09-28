package dev.faizarfi.starter.arfiid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthTokenRequest {

    // must be "authorization_code" for authorization code flow or "refresh_token"
    @NotBlank(message = "Grant type is required")
    @JsonProperty("grant_type")
    private String grantType;

    // optional for refresh_token, required for authorization_code
    private String code;

    // option for refresh_token
    @JsonProperty("redirect_uri")
    private String redirectUri;

    // required for refresh_token grant
    @JsonProperty("refresh_token")
    private String refreshToken;

    @NotBlank(message = "Client ID is required")
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank(message = "Client secret is required")
    @JsonProperty("client_secret")
    private String clientSecret;
}
