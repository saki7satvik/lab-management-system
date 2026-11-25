# Security Architecture

## 1. Authentication & JWT Specification
- **Token Issuer**: `user-service` (`POST /auth/login`).
- **Algorithm**: HMAC-SHA256 (`HS256`) via `io.jsonwebtoken` (`jjwt` `0.11.5`).
- **Signing Secret**: Hardcoded in `JwtUtil` across `api-gateway` and all 5 microservices:
  `your-very-secret-key-which-is-long-enough-12345678901234567890`
- **Expiration**: `3600000 ms` (1 hour).
- **Claims**:
  - `sub`: User `collegeId`
  - `role`: `SUPER_ADMIN` | `ADMIN` | `INSTRUCTOR` | `STUDENT`
  - `status`: `ACTIVE` | `INACTIVE` | `SUSPENDED`
  - `iat`, `exp`

## 2. Gateway & Service Enforcement
1. **API Gateway (`JwtAuthenticationFilter`)**:
   - Skips `OPTIONS` and `RouteValidator.isSecured == false` (`/auth/login`, `/auth/register`, `/eureka`, `/v3/api-docs`, `/swagger-ui`, `/*-service/v3/api-docs`).
   - Validates `Authorization: Bearer <token>` signature & expiry via `JwtUtil.isTokenValid()`. Returns `401` on failure.
   - Forwards `Authorization` header downstream.
2. **Downstream Microservices (`JwtFilter` + `SecurityConfig`)**:
   - Stateless `SessionCreationPolicy.STATELESS`, CSRF disabled, CORS disabled.
   - Permits `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html`, `/auth/**`.
   - `JwtFilter` re-validates JWT, rejects non-`ACTIVE` status (`403`), constructs `CurrentUser(collegeId, role, status)` principal with `ROLE_<role>` authority.
   - `@EnableMethodSecurity` enforces `@PreAuthorize` on controller endpoints, complemented by service-layer role/ownership checks.
3. **Feign Propagation (`FeignConfig`)**:
   - `request-service`, `issue-service`, and `return-service` copy `Authorization` from `RequestContextHolder` to outgoing Feign requests.
4. **Frontend Security**:
   - `TokenService` stores JWT in `localStorage['token']`.
   - `jwtInterceptor` attaches `Authorization: Bearer <token>` to all HTTP requests and clears token + redirects to `/login` on `401`.
   - `authGuard` protects `/dashboard/**`.

## 3. Password & Seed Credentials
- **Hashing**: `BCryptPasswordEncoder` (default strength).
- **Bootstrap Super Admin**: `DataInitializer` in `user-service` seeds `SUPER_ADMIN_001` (`superadmin@lab.com`) with password `welcome@123`.
- **New Users**: `UserServiceImpl.createUser()` assigns `welcome@123` to every newly created user.

## 4. Security Vulnerabilities & Gaps
1. **Gateway Path-Bypass Vulnerability**: `RouteValidator` uses `request.getURI().getPath().contains(uri)`. Any secured URL containing `/auth/login`, `/eureka`, or `/swagger-ui` as a substring bypasses gateway JWT validation.
2. **Hardcoded Secrets**: JWT signing key (`JwtUtil`) and MySQL root credentials (`application.properties`) are hardcoded in source.
3. **Hardcoded Default Password & No Password Reset**: All users start with `welcome@123` and there is no password-change or reset endpoint.
4. **Token Revocation Gap**: Microservices trust the JWT `status` claim without checking the database; deactivated/suspended users remain authorized until the 1-hour token expires.
5. **Frontend `roleGuard` Unused**: `roleGuard` is not attached to any Angular route; role restrictions in the frontend are UI-only.
6. **Sensitive Console Logging & XSS Exposure**: Frontend `Auth.login()` logs credentials and JWT payloads to `console.log`, and storing the JWT in `localStorage` exposes it to XSS.
7. **Inconsistent Auth Error Formats**: `JwtFilter` uses `response.sendError()` (plain text/container default) instead of `ErrorResponse` JSON, and `AuthController.login` returns `403` instead of `401` on bad credentials.
8. **`JwtUtil` Platform Charset**: `SECRET.getBytes()` uses the JVM default charset rather than explicit `StandardCharsets.UTF_8`.
