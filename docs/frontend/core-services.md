# Frontend Core Services, Guards & Interceptors

## 1. Core Services (`src/app/core/services/`)

### 1.1 `Auth` (`auth.ts`)
- **Injectable**: `providedIn: 'root'` (class name is `Auth`, not `AuthService`).
- **Dependencies**: `HttpClient`, `TokenService`, `API = environment.apiUrl`.
- **Methods**:
  - `login(data: LoginRequest): Observable<LoginResponse>`
    - Sends `POST ${API}/auth/login` with `data`.
    - On response, calls `this.tokenService.setToken(res.token)`.
    - **Security Flaw**: Logs request payload, response, and decoded token payload via `console.log`.
  - `logout(): void`
    - Calls `this.tokenService.clearToken()`. No backend logout request is made.

### 1.2 `TokenService` (`token.ts`)
- **Injectable**: `providedIn: 'root'`
- **Storage Key**: `TOKEN_KEY = 'token'` in `localStorage`.
- **Methods**:
  - `setToken(token: string): void` — `localStorage.setItem('token', token)`
  - `getToken(): string | null` — `localStorage.getItem('token')`
  - `clearToken(): void` — `localStorage.removeItem('token')`
  - `getPayload(): any` — Splits JWT on `.`, decodes index `1` with `atob()`, parses JSON; returns `null` on error. (Note: `atob()` can fail on base64url `-`/`_` characters).
  - `isTokenExpired(): boolean` — Returns `true` if payload or `payload.exp` is missing, or `Date.now() >= payload.exp * 1000`.
  - `isLoggedIn(): boolean` — Checks `!!getToken()` and `!isTokenExpired()`; clears token if expired.
  - `getRole(): string | null` — Returns `payload?.role ?? null`.
  - `getCollegeId(): string | null` — Returns `payload?.sub ?? null`.
  - `getStatus(): string | null` — Returns `payload?.status ?? null`.

## 2. Route Guards (`src/app/core/guards/`)

### 2.1 `authGuard` (`auth.guard.ts`)
- Functional `CanActivateFn`.
- Injects `TokenService` and `Router`.
- If `tokenService.isLoggedIn()` is `true`, returns `true`; otherwise navigates to `/login` and returns `false`.

### 2.2 `roleGuard` (`role.guard.ts`)
- Functional `CanActivateFn`.
- Reads `route.data['roles']` and compares against `tokenService.getRole()`.
- If included, returns `true`; otherwise navigates to `/login` and returns `false`.
- **Flaw**: Unused in `app.routes.ts` and all feature route files; throws if `route.data['roles']` is undefined.

## 3. HTTP Interceptor (`src/app/core/interceptor/jwt.interceptor.ts`)
- Functional `HttpInterceptorFn` registered in `app.config.ts`.
- Reads `TokenService.getToken()`. If present, clones request with `Authorization: Bearer <token>` (applies to all outgoing requests without origin filtering).
- Catches HTTP errors:
  - On `error.status === 401`: calls `tokenService.clearToken()` and navigates to `/login`.
  - Re-throws all errors via `throwError(() => error)`.
- **Gaps**: No `403` handling, no refresh-token flow, no centralized error/toast notification service.

## 4. Core Models (`src/app/core/models/`)
- `MenuItem` (`menu-item.model.ts`): `{ label: string; icon?: string; route: string; }`
