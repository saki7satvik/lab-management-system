# Frontend Architecture

## 1. Technology Stack
- **Framework**: Angular `^21.2.0` (standalone components, functional guards, functional HTTP interceptors, `@angular/router` lazy loading)
- **Language**: TypeScript `~5.9.2`
- **UI & Styling**: Angular Material `^21.2.10` (theme configured in `src/styles.scss` with Azure/Blue palettes, though feature templates primarily use native HTML elements with custom glassmorphism SCSS)
- **Reactive State**: RxJS `~7.8.0` (`BehaviorSubject`, `combineLatest`, `switchMap`, `shareReplay`, `tap`, `catchError`)
- **Testing**: Vitest `^4.0.8` with `jsdom` `^28.0.0` (`ng test`)
- **Build Tool**: `@angular/build:application` (`^21.2.9`), `npm@11.0.0`

## 2. Application Bootstrap & Configuration
- **Entry Point**: `frontend/src/main.ts` calls `bootstrapApplication(App, appConfig)`.
- **Root Component**: `App` (`src/app/app.ts`) renders `<router-outlet></router-outlet>` (`app.html`).
- **App Config** (`src/app/app.config.ts`):
  - `provideBrowserGlobalErrorListeners()`
  - `provideRouter(routes)`
  - `provideHttpClient(withInterceptors([jwtInterceptor]))`
- **Environments**:
  - Development (`src/environments/environment.ts`): `production: false`, `apiUrl: 'http://localhost:8080'`
  - Production (`src/environments/environment.prod.ts`): `production: true`, `apiUrl: 'https://your-production-api.com'` (placeholder)

## 3. Directory Structure
```text
frontend/src/
├── index.html                    # Loads Roboto & Material Icons; title "Frontend"
├── main.ts
├── styles.scss                   # Global reset, Angular Material mat.theme(), background image
├── assests/images/login-bg-img.jpg # Note: directory spelled "assests"
├── environments/
│   ├── environment.ts
│   └── environment.prod.ts
├── styles/
│   ├── _variable.scss            # CSS custom properties (--glass-bg, --primary-cyan, etc.)
│   ├── _glass.scss               # .glass-panel mixin/class
│   └── _layout.scss              # .page-container, .page-header, .page-content (not imported globally)
└── app/
    ├── app.config.ts
    ├── app.routes.ts
    ├── app.ts / .html / .scss
    ├── core/
    │   ├── constants/menu.config.ts
    │   ├── guards/auth.guard.ts
    │   ├── guards/role.guard.ts  # Defined but unused in routes
    │   ├── interceptor/jwt.interceptor.ts
    │   ├── models/menu-item.model.ts
    │   └── services/auth.ts
    │   └── services/token.ts
    ├── layouts/
    │   ├── auth-layout/
    │   └── dashboard-layout/
    ├── shared/components/
    │   ├── feature-card/
    │   ├── page-header/
    │   ├── profile-menu/
    │   └── sidebar/
    └── features/
        ├── auth/
        ├── users/
        ├── inventory/
        ├── requests/
        └── returns/
```

## 4. Routing & Navigation Architecture
Defined in `src/app/app.routes.ts`:
- `/` (`AuthLayout`):
  - `/login` → `LoginPage` (eagerly imported)
  - `""` → `redirectTo: 'login'` (`pathMatch: 'full'`)
- `/dashboard` (`DashboardLayout`, guarded by `authGuard`):
  - `/dashboard/users` → lazy-loads `USERS_ROUTES`
  - `/dashboard/inventory` → lazy-loads `INVENTORY_ROUTES`
  - `/dashboard/requests` → lazy-loads `REQUESTS_ROUTES`
  - `/dashboard/returns` → lazy-loads `RETURNS_ROUTES`
  - `""` → `redirectTo: 'login'` (`pathMatch: 'full'`)
- **No wildcard (`**`) route** and **no `/dashboard/system` route** (despite `LoginForm` redirecting `SUPER_ADMIN` to `/dashboard/system`).
- **No `roleGuard` applied** to any route; role restrictions are enforced only via UI visibility (`MENU_CONFIG` and `@if` blocks).

## 5. Authentication & HTTP Pipeline
1. `TokenService` stores the raw JWT in `localStorage` under key `'token'` and decodes the middle segment with `atob()`.
2. `jwtInterceptor` attaches `Authorization: Bearer <token>` to all outgoing `HttpClient` requests when a token exists. On `401` responses, it clears the token and redirects to `/login`. `403` responses are not handled globally.
3. Role-based menu items in `MENU_CONFIG` (`src/app/core/constants/menu.config.ts`) dynamically render sidebar links in `Sidebar` component.

## 6. Known Architectural Flaws in Frontend
- `roleGuard` is implemented but never attached to routes.
- `/dashboard` empty path redirects to `/login` instead of a dashboard home.
- `SUPER_ADMIN` post-login redirect targets non-existent `/dashboard/system`.
- `issue-service` has no corresponding frontend feature module.
- Several scaffolded components (`UserTable`, `UserForm`, `UserActions`, `ComponentFilters`, `ComponentTable`, `StockUpdateDialog`, `StockHistoryPage`) are empty placeholders.
- No centralized notification/toast or error service; components use native `alert()` / `confirm()` or ignore stream errors.
