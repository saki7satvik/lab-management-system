# Service: `api-gateway`

## 1. Overview & Configuration
- **Module Path**: `backend/api_gateway/api_gateway/`
- **Artifact**: `com:api_gateway:0.0.1-SNAPSHOT` (Spring Boot `3.5.13`, Spring Cloud `2025.0.2`, Java 17)
- **Dependencies**: `spring-cloud-starter-gateway` (Reactive/WebFlux), `spring-cloud-starter-netflix-eureka-client`, `spring-cloud-starter-loadbalancer`, `jjwt` (`0.11.5`), `spring-boot-starter-validation`, `lombok`, `springdoc-openapi-starter-webflux-ui` (`2.3.0`).
- **Main Class**: `com.api_gateway.ApiGatewayApplication` (`@SpringBootApplication`, `@EnableDiscoveryClient`).
- **Port**: `8080`.

## 2. Routing & CORS (`application.properties`)
- **CORS**:
  - `allowed-origins=http://localhost:4200`
  - `allowed-methods=GET,POST,PUT,DELETE,OPTIONS`
  - `allowed-headers=*`, `allow-credentials=true`
  - `default-filters[0]=DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_UNIQUE`
- **Service Routes**:
  - `user-service`: `Path=/users/**` → `lb://USER-SERVICE`
  - `auth-service`: `Path=/auth/**` → `lb://USER-SERVICE`
  - `inventory-service`: `Path=/components/**` → `lb://INVENTORY-SERVICE`
  - `request-service`: `Path=/requests/**` → `lb://REQUEST-SERVICE`
  - `issue-service`: `Path=/issues/**` → `lb://ISSUE-SERVICE`
  - `return-service`: `Path=/returns/**` → `lb://RETURN-SERVICE`
- **Aggregated Swagger Routes** (`/user-service/v3/api-docs`, `/inventory-service/v3/api-docs`, `/request-service/v3/api-docs`, `/issue-service/v3/api-docs`, `/return-service/v3/api-docs`):
  - Each rewrites `/<service>/v3/api-docs` → `/v3/api-docs` and configures `springdoc.swagger-ui.urls[*]`.

## 3. Security & Filtering
- **`RouteValidator`** (`com.api_gateway.security.RouteValidator`):
  - `OPEN_API_ENDPOINTS`: `/auth/login`, `/auth/register`, `/eureka`, `/v3/api-docs`, `/swagger-ui`, `/swagger-ui.html`, and the 5 `/<service>/v3/api-docs` paths.
  - `isSecured`: `OPEN_API_ENDPOINTS.stream().noneMatch(uri -> request.getURI().getPath().contains(uri))`.
- **`JwtAuthenticationFilter`** (`com.api_gateway.filter.JwtAuthenticationFilter`, implements `GlobalFilter`):
  - Skips `HttpMethod.OPTIONS` and non-secured routes (`!routeValidator.isSecured.test(request)`).
  - Requires `Authorization: Bearer <token>` header; validates via `JwtUtil.isTokenValid(token)`.
  - On missing/invalid token, sets `HttpStatus.UNAUTHORIZED` (401) and completes the response.
  - Passes the original `Authorization` header downstream unchanged.
- **`JwtUtil`**: Hardcoded HS256 secret `"your-very-secret-key-which-is-long-enough-12345678901234567890"`, 1-hour expiration.

## 4. Flaws & Gaps
- **`RouteValidator` Substring Bypass**: Uses `path.contains(uri)` instead of exact/prefix matching; any secured path containing e.g. `/auth/login` or `/eureka` anywhere in the URI bypasses gateway authentication.
- **Dead Exception Handler**: `GlobalExceptionHandler` (`@RestControllerAdvice`) and `UnauthorizedException` do not intercept WebFlux `GlobalFilter` errors, and `JwtAuthenticationFilter` completes responses directly without JSON bodies.
- **Hardcoded JWT Secret** in `JwtUtil.java`.
- `/auth/register` is listed in `OPEN_API_ENDPOINTS` even though no register endpoint exists in `user-service`.
