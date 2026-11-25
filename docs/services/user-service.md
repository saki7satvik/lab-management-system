# Service: `user-service`

## 1. Overview & Configuration
- **Module Path**: `backend/user_service/user_service/`
- **Artifact**: `com:user_service:0.0.1-SNAPSHOT` (Spring Boot `3.5.13`, Spring Cloud `2025.0.2`, Java 17)
- **Port**: `8081` (`spring.application.name=user-service`)
- **Database**: `jdbc:mysql://localhost:3306/lab_management_user_db` (`ddl-auto=update`)
- **Log File**: `logs/user-service.log`

## 2. Data Model (`com.user_service`)
- **Entity `User`** (`@Table(name = "users")`):
  - `id` (`String`, UUID `@Id`)
  - `collegeId` (`String`, unique, not null)
  - `name` (`String`)
  - `email` (`String`, unique, not null)
  - `phone` (`String`)
  - `password` (`String`, not null, BCrypt hash)
  - `role` (`Role`: `SUPER_ADMIN`, `ADMIN`, `INSTRUCTOR`, `STUDENT`)
  - `status` (`Status`: `ACTIVE`, `INACTIVE`, `SUSPENDED`)
- **Repository `UserRepository`**:
  - `existsByRole(Role)`, `existsByCollegeId(String)`, `existsByEmail(String)`, `findByCollegeId(String)`
- **Startup Seed (`DataInitializer`)**:
  - If `SUPER_ADMIN_001` is absent, creates `Super Admin` (`SUPER_ADMIN_001`, `superadmin@lab.com`, `0000000000`, `SUPER_ADMIN`, `ACTIVE`, password `welcome@123`).

## 3. API Endpoints

### `AuthController` (`/auth`)
- `POST /auth/login` *(Public)*
  - Request: `LoginRequestDTO` (`collegeId`, `password` — no `@Valid`)
  - Response: `200 OK` `LoginResponseDTO` (`{ token }`)
  - Logic: Finds user by `collegeId`, verifies BCrypt password, requires `Status.ACTIVE`, generates HS256 JWT (`sub=collegeId`, `role`, `status`, 1h expiry). Invalid credentials or inactive user throw `UnauthorizedException` (mapped to `403` by `GlobalExceptionHandler`).

### `UserController` (`/users`)
- `POST /users` — `@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")`
  - Request: `@Valid CreateUserDTO` (`name` `@NotBlank`, `collegeId` `@NotBlank`, `email` `@Email @NotBlank`, `phone`, `role` `@NotNull`)
  - Response: `201 CREATED` `UserResponseDTO` (`name`, `collegeId`, `email`, `phone`, `role`, `status`)
  - Logic: Requires active creator; enforces `creator.role.ordinal() < dto.role.ordinal()`; checks duplicate `collegeId` / `email` (`409`); sets `Status.ACTIVE` and default password `welcome@123`.
- `GET /users` — Authenticated (any role)
  - Response: `200 OK` `List<UserResponseDTO>`
  - Logic: Requires active caller; if `STUDENT`, filters to `ACTIVE` users only; filters to targets where `currentRole.ordinal() <= targetRole.ordinal()`.
- `GET /users/{collegeId}` — Authenticated (any role)
  - Response: `200 OK` `UserResponseDTO`
  - Logic: Requires active caller and active target (`404` if inactive/suspended); enforces `currentRole.ordinal() <= targetRole.ordinal()`.
- `PUT /users/{collegeId}/deactivate` — `@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")`
  - Response: `200 OK` `"User deactivated"` (`String`)
  - Logic: Cannot modify self or `SUPER_ADMIN`; requires `currentRole.ordinal() < targetRole.ordinal()`; target must be `ACTIVE`; sets `INACTIVE`.
- `PUT /users/{collegeId}/reactivate` — `@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")`
  - Response: `200 OK` `UserResponseDTO`
  - Logic: Same hierarchy checks; target must be `INACTIVE` (`SUSPENDED` cannot be reactivated); sets `ACTIVE`.

## 4. Security, Exceptions & AOP
- `SecurityConfig`: Stateless, CSRF/CORS disabled, permits `OPTIONS /**`, `/auth/**`, Swagger paths. `JwtFilter` rejects non-`ACTIVE` tokens (`403`) and invalid tokens (`401`) via `sendError`.
- `GlobalExceptionHandler`: Maps `BadRequestException` (400), `ResourceNotFoundException` (404), `UnauthorizedException` (403), `AuthorizationDeniedException` (403), `ConflictException` (409), `MethodArgumentNotValidException` (400), `Exception` (500).
- `aop/`: `LoggingAspect`, `PerformanceAspect`, `ExceptionAspect`.

## 5. Flaws & Gaps
- Hardcoded JWT secret and hardcoded default password (`welcome@123`) with no password-change endpoint.
- `UnauthorizedException` in `AuthController.login` returns `403 Forbidden` instead of `401 Unauthorized`.
- `SUSPENDED` users cannot be reactivated (`reactivateUser` checks `status == INACTIVE`).
- No `@Valid` on `LoginRequestDTO`.
- `JwtFilter` does not check DB status on existing tokens, so deactivated users retain access until token expiry.
