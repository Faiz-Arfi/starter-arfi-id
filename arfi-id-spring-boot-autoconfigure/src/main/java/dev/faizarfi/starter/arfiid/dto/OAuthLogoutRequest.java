package dev.faizarfi.starter.arfiid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthLogoutRequest {
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
}
