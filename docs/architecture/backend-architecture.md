# Backend Architecture

## 1. Microservices Overview
The backend is a Spring Boot 3.5.x / Spring Cloud 2025.0.2 microservices suite targeting Java 17 and Maven (`mvnw` wrapper included per service). Each service is an independent Maven project under `backend/<service>/<service>/`.

| Service | Artifact ID | Spring App Name | Port | Database |
|---|---|---|---|---|
| Eureka Server | `eureka` | `eureka-server` | `8761` | None |
| API Gateway | `api_gateway` | `api-gateway` | `8080` | None |
| User Service | `user_service` | `user-service` | `8081` | `lab_management_user_db` |
| Inventory Service | `inventory_service` | `inventory-service` | `8082` | `lab_management_inventory_db` |
| Request Service | `request_service` | `request-service` | `8083` | `lab_management_request_db` |
| Issue Service | `issue-service` | `issue-service` | `8084` | `lab_management_issue_db` |
| Return Service | `return-service` | `return-service` | `8085` | `lab_management_return_db` |

## 2. Common Microservice Patterns
All five domain microservices (`user-service`, `inventory-service`, `request-service`, `issue-service`, `return-service`) share an identical architectural template:
- **Layered Structure**: `controller` → `service` (interface + `impl`) → `repository` (Spring Data JPA) → `entity` (UUID String `@Id`).
- **Security**:
  - Stateless Spring Security (`SessionCreationPolicy.STATELESS`), CSRF disabled, CORS disabled (handled at API Gateway).
  - `@EnableMethodSecurity` with `@PreAuthorize` on controller methods.
  - Custom `JwtFilter` (`OncePerRequestFilter`) validates HS256 JWTs using `jjwt` 0.11.5 with a hardcoded shared secret, extracts `collegeId` (`sub`), `role`, `status`, rejects non-`ACTIVE` users (`403`), and populates `SecurityContextHolder` with `CurrentUser` principal and `ROLE_<ROLE>` authority.
  - `CustomAuthenticationEntryPoint` (401 JSON) and `CustomAccessDeniedHandler` (403 JSON).
- **Exception Handling**:
  - `@RestControllerAdvice` `GlobalExceptionHandler` mapping `BadRequestException` (400), `ResourceNotFoundException` (404), `UnauthorizedException` (403), `AuthorizationDeniedException` (403), `ConflictException` (409), `ServiceUnavailableException` (503, in Feign services), `MethodArgumentNotValidException` (400, first field error only), and `Exception` (500).
- **AOP Cross-Cutting Concerns** (`aop/` package):
  - `LoggingAspect`: Logs controller entry (`@Before`) and service return (`@AfterReturning`).
  - `PerformanceAspect`: Logs service method execution duration in ms (`@Around`).
  - `ExceptionAspect`: Logs thrown exceptions (`@AfterThrowing`).
- **OpenAPI / Swagger**:
  - `springdoc-openapi-starter-webmvc-ui` 2.7.0 (2.3.0 webflux in `api-gateway`).
  - `OpenApiConfig` configures `bearerAuth` HTTP Bearer JWT scheme.
  - `api-gateway` aggregates all 5 services' `/v3/api-docs` under `/<service>/v3/api-docs`.
- **Logging**:
  - Rolling file logs under `logs/<service-name>.log` (10MB max file size, 7-day history).
  - Note: All services mistakenly configure `logging.level.com.user_service=DEBUG` (copy-pasted from `user-service`).

## 3. Inter-Service Communication
- Synchronous HTTP via **Spring Cloud OpenFeign** resolved through Eureka service names (`INVENTORY-SERVICE` / `inventory-service`, `REQUEST-SERVICE` / `request-service`).
- `FeignConfig` in `request-service`, `issue-service`, and `return-service` registers a `RequestInterceptor` that copies the incoming `Authorization` header from `RequestContextHolder` to outgoing Feign requests.

## 4. Architectural Flaws & Gaps
- **Distributed Transaction Consistency**: `request-service`, `issue-service`, and `return-service` perform remote stock mutations in `inventory-service` inside local `@Transactional` methods before or alongside local DB saves, without Saga/compensation logic.
- **Duplicate Stock Deduction**: Both `request-service` (on `approve`) and `issue-service` (on `issueComponents`) call `POST /components/deduct`, deducting stock twice if both workflows are used for a request.
- **No Optimistic/Pessimistic Locking**: Concurrent stock updates or request approvals/issuances/returns can race and violate quantity invariants.
- **Unsecured Internal Endpoints**: `inventory-service` `/components/validate` and `/components/availability` are intended as internal endpoints but rely on forwarding the end-user's JWT rather than service-to-service auth.
- **Hardcoded Secrets & Config**: JWT secret and MySQL root credentials are hardcoded/duplicated across services.
