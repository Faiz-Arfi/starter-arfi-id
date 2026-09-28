package dev.faizarfi.starter.arfiid.service;

import dev.faizarfi.starter.arfiid.config.ArfiProperties;
import dev.faizarfi.starter.arfiid.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class ArfiAuthServiceClient {

    private final ArfiProperties properties;
    private final RestTemplate restTemplate;

    public AuthResponse exchangeCodeWithArfiId(ExchangeCodeRequest request) {
        String tokenEndpoint = "/auth/oauth/token";
        log.debug("Initiating server-to-server token exchange with Arfi ID at {}", tokenEndpoint);

        OAuthTokenRequest payloadRequest = OAuthTokenRequest.builder()
                .grantType(request.getGrantType())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .code(request.getCode())
                .redirectUri(request.getRedirectUri())
                .build();

        return makeRequestToArfiId(tokenEndpoint, payloadRequest, new ParameterizedTypeReference<>() {});
        
    }

    public void revokeSessionAtArfiId(String refreshToken) {
        OAuthLogoutRequest payload = OAuthLogoutRequest.builder()
                .refreshToken(refreshToken)
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .build();
        LogOutResponse response = makeRequestToArfiId("/auth/oauth/logout", payload, new ParameterizedTypeReference<>() {});
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        String tokenEndpoint = "/auth/oauth/token";

        OAuthTokenRequest payload = OAuthTokenRequest.builder()
                .grantType("refresh_token")
                .refreshToken(refreshToken)
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .build();

        return makeRequestToArfiId(tokenEndpoint, payload, new ParameterizedTypeReference<>() {});
    }

    private <T> T makeRequestToArfiId(String endpoint, Object payload, ParameterizedTypeReference<ApiResponse<T>> responseType) {

        String url = properties.getIssuer() + endpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        try {

            ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);

            ApiResponse<T> apiResponse = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Arfi ID request failed: " + response.getStatusCode());
            }

            if (apiResponse == null) {
                throw new RuntimeException("Arfi ID returned an empty response");
            }

            if (!apiResponse.isSuccess()) {
                throw new RuntimeException("Arfi ID request failed: " + apiResponse.getMessage());
            }

            return apiResponse.getData();

        } catch (Exception e) {

            log.error("Error during Arfi ID request to {}", endpoint, e);

            throw new RuntimeException("Arfi ID request failed", e);
        }
    }
}