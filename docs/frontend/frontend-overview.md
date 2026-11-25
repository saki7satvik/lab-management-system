# Frontend Overview

## 1. Project Metadata
- **Package Name**: `lab-management-frontend` (`frontend/package.json`)
- **Angular Version**: `^21.2.0` (CLI `^21.2.9`, `@angular/build:application`)
- **TypeScript**: `~5.9.2` (`tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`)
- **Test Runner**: Vitest `^4.0.8` with `jsdom` `^28.0.0` (`npm test` / `ng test`)
- **Dev Server**: `ng serve` on `http://localhost:4200`

## 2. Application Configuration
- **Bootstrap**: `src/main.ts` bootstraps `App` (`src/app/app.ts`) with `appConfig` (`src/app/app.config.ts`).
- **Providers** (`app.config.ts`):
  - `provideBrowserGlobalErrorListeners()`
  - `provideRouter(routes)`
  - `provideHttpClient(withInterceptors([jwtInterceptor]))`
- **Environments**:
  - `src/environments/environment.ts`: `{ production: false, apiUrl: 'http://localhost:8080' }`
  - `src/environments/environment.prod.ts`: `{ production: true, apiUrl: 'https://your-production-api.com' }`

## 3. Root Routing (`src/app/app.routes.ts`)
```text
/                              → AuthLayout
  ├── ''                       → redirectTo: 'login' (pathMatch: 'full')
  └── 'login'                  → LoginPage
/dashboard                     → DashboardLayout (canActivate: [authGuard])
  ├── ''                       → redirectTo: 'login' (pathMatch: 'full')  [FLAW: redirects authenticated users to /login]
  ├── 'users'                  → loadChildren: () => import('./features/users/users.routes').then(m => m.USERS_ROUTES)
  ├── 'inventory'              → loadChildren: () => import('./features/inventory/inventory.routes').then(m => m.INVENTORY_ROUTES)
  ├── 'requests'               → loadChildren: () => import('./features/requests/requests.routes').then(m => m.REQUESTS_ROUTES)
  └── 'returns'                → loadChildren: () => import('./features/returns/returns.routes').then(m => m.RETURNS_ROUTES)
```
- **Missing**: No wildcard `**` route, no `/dashboard/system` route, and `roleGuard` is not attached to any route.

## 4. Styling & Theme System
- **`src/index.html`**: Loads Google Fonts (`Roboto` 300/400/500) and `Material Icons`.
- **`src/styles.scss`**:
  - Configures Angular Material 3 theme via `mat.theme((color: (primary: mat.$azure-palette, tertiary: mat.$blue-palette), typography: Roboto, density: 0))`.
  - Applies dark background overlay over `./assests/images/login-bg-img.jpg` (note misspelled `assests/` directory).
  - Sets `font-family` referencing `Inter` (not loaded in `index.html`) and `overflow-x: hidden`.
- **`src/styles/_variable.scss`**: Defines CSS variables `--glass-bg`, `--glass-border`, `--glass-shadow`, `--glass-blur`, `--primary-blue`, `--primary-cyan`, `--text-primary`, `--text-secondary`, `--danger`, `--success`, `--border-radius`.
- **`src/styles/_glass.scss`**: Defines `.glass-panel` glassmorphism utility class.
- **`src/styles/_layout.scss`**: Defines `.page-container`, `.page-header`, `.page-content` (not imported in `styles.scss`).
- **`src/app/app.scss`**: Duplicates `body` background styling from `styles.scss`.
