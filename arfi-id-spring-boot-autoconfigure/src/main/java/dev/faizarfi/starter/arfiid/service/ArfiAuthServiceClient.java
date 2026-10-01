package dev.faizarfi.starter.arfiid.service;

import dev.faizarfi.starter.arfiid.config.ArfiProperties;
import dev.faizarfi.starter.arfiid.dto.*;
import dev.faizarfi.starter.arfiid.exception.ArfiAuthenticationException;
import dev.faizarfi.starter.arfiid.exception.ArfiServiceException;
import dev.faizarfi.starter.arfiid.exception.ArfiStarterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
        makeRequestToArfiId("/auth/oauth/logout", payload, new ParameterizedTypeReference<>() {});
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

    /**
     * Sends a request to the Arfi ID authentication service and extracts the
     * response payload from the standardized Arfi ID API response.
     *
     * <p>This method centralizes communication with Arfi ID and translates
     * authentication-related failures and service-level failures into the
     * starter's domain-specific exceptions.</p>
     *
     * <p>HTTP 401 and 403 responses are treated as authentication failures.
     * Other HTTP client errors, server errors, communication failures, and
     * malformed responses are treated as service-level failures.</p>
     *
     * @param endpoint the Arfi ID API endpoint to call
     * @param payload the request body sent to Arfi ID
     * @param responseType the parameterized response type used to deserialize
     *                     the Arfi ID response
     * @param <T> the type of data contained in the API response
     * @return the response data returned by Arfi ID
     * @throws ArfiAuthenticationException if Arfi ID rejects authentication
     *                                     or authorization
     * @throws ArfiServiceException if communication with Arfi ID fails or
     *                              Arfi ID returns an unexpected response
     * @throws ArfiStarterException if a starter-specific exception has already
     *                              been created during request processing
     */
    private <T> T makeRequestToArfiId(
            String endpoint,
            Object payload,
            ParameterizedTypeReference<ApiResponse<T>> responseType
    ) {

        String url = properties.getIssuer() + endpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        try {

            ResponseEntity<ApiResponse<T>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            responseType
                    );

            ApiResponse<T> apiResponse = response.getBody();

            if (apiResponse == null) {
                throw new ArfiServiceException(
                        "Arfi ID returned an empty response"
                );
            }

            if (!apiResponse.isSuccess()) {

                int status = apiResponse.getStatus();
                String message = apiResponse.getMessage();

                if (status == 401 || status == 403) {
                    throw new ArfiAuthenticationException(
                            "Authentication failed with Arfi ID: " + message
                    );
                }

                throw new ArfiServiceException(
                        "Arfi ID request failed: " + message
                );
            }

            return apiResponse.getData();

        } catch (ArfiStarterException e) {

            // Preserve exceptions that have already been classified
            // by the starter.
            throw e;

        } catch (HttpClientErrorException e) {

            log.warn(
                    "HTTP client error during Arfi ID request to {}: {}",
                    endpoint,
                    e.getStatusCode()
            );

            if (e.getStatusCode().value() == 401
                    || e.getStatusCode().value() == 403) {

                throw new ArfiAuthenticationException(
                        "Authentication failed with Arfi ID: "
                                + e.getStatusText(),
                        e
                );
            }

            throw new ArfiServiceException(
                    "Arfi ID rejected the request: "
                            + e.getStatusText(),
                    e
            );

        } catch (HttpServerErrorException e) {

            log.error(
                    "Server error during Arfi ID request to {}: {}",
                    endpoint,
                    e.getStatusCode(),
                    e
            );

            throw new ArfiServiceException(
                    "Arfi ID service encountered an internal error",
                    e
            );

        } catch (Exception e) {

            log.error(
                    "Error during Arfi ID request to {}",
                    endpoint,
                    e
            );

            throw new ArfiServiceException(
                    "Failed to communicate with Arfi ID service",
                    e
            );
        }
    }
}