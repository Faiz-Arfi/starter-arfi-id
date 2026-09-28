# Arfi ID Spring Boot Starter (starter-arfi-id)

A production-grade, auto-configured Spring Boot starter library designed for seamless integration of microservices with Arfi ID, an Identity Provider platform and Authentication Platfrom.

## Architectural Overview

The starter uses a multi-module Maven architecture to separate core autoconfiguration logic from consumer dependency aggregation:
````
starter-arfi-id/
├── arfi-id-spring-boot-autoconfigure/  # Core autoconfiguration, security filters, services, and default controllers
└── arfi-id-spring-boot-starter/        # Lightweight aggregator module for clean downstream inclusion

````

### Key Features
* **Auto-Configuration:** Automatically registers security filters and endpoints using Spring Boot's standardized `AutoConfiguration.imports` mechanism.
* **Stateless JWT Cookie Authentication:** Intercepts incoming requests, extracts secure namespaced HttpOnly tokens, and populates the Spring Security context.
* **Fail-Fast Startup Validation:** Leverages Jakarta Bean Validation (`@NotBlank`) on `@ConfigurationProperties` to ensure misconfigured clients fail immediately at application bootstrap.
* **Out-of-the-Box Endpoints:** Inherits pre-built endpoints (`/arfiid/callback`, `/arfiid/refresh`, `/arfiid/me`, `/arfiid/logout`) without requiring custom controller boilerplate in consuming microservices.

---

## Installation & Consumption

### 1. Adding the Dependency

Add the starter dependency to your consumer microservice `pom.xml`:

```xml
<dependency>
    <groupId>dev.faizarfi</groupId>
    <artifactId>arfi-id-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

```

### 2. Required Configuration Properties

Configure the required properties in your application YAML configuration. If any required property is missing, validation rules will halt startup:

```yaml
arfi-id:
  issuer: [https://api.id.faizarfi.dev](https://api.id.faizarfi.dev)        # Central Arfi ID Provider URL (or http://localhost:8080 for local development)
  client-id: your-unique-client-id           # Registered client identifier
  client-secret: your-secure-client-secret   # Shared HMAC client secret for token verification
  redirect-uri: [https://yourapp.dev/callback](https://yourapp.dev/callback) # OAuth callback redirect URI
  cookie-prefix: app_                        # Namespaced cookie prefix (e.g., app_accessToken, app_refreshToken)

```

---

## Security Lifecycle & Endpoints

Importing the starter automatically exposes the following managed endpoints under the `/arfiid` context path:

* **POST /arfiid/callback**: Handles server-to-server code exchange with Arfi ID and establishes secure, namespaced `HttpOnly`, `SameSite=Lax` cookies.
* **POST /arfiid/refresh**: Reads the refresh cookie, requests a rotated access token from Arfi ID, and updates client session cookies.
* **GET /arfiid/me**: Returns the currently authenticated user email, role, and authentication status from the security context.
* **POST /arfiid/logout**: Notifies Arfi ID to revoke the remote session and wipes local browser cookies.

---

## Technology Stack

* **Java:** 25
* **Spring Boot:** 4.1.1 (Spring Framework 7 / Jakarta EE 11)
* **Token Management:** JJWT (`0.12.6`) with HMAC-SHA key verification
* **Validation:** Jakarta Bean Validation API

---

## Final Verdict

This project is a Spring Boot starter library that simplifies the integration of microservices with Arfi ID, an Identity Provider platform. It provides auto-configuration, stateless JWT cookie authentication, fail-fast startup validation, and out-of-the-box endpoints for authentication and user management. By using this starter, developers can easily secure their applications without writing boilerplate code, ensuring a smooth and efficient authentication process.

-FaizArfi | [Arfi ID](https:id.faizarfi.dev) | [faizarfi.dev](https:faizarfi.dev)

