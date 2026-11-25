# Frontend Feature: Auth

## 1. Routing
There is no `auth.routes.ts`. Auth routes are declared directly in `src/app/app.routes.ts` under `AuthLayout`:
- `/login` → `LoginPage`
- `/` → redirects to `/login`

## 2. Models (`src/app/features/auth/models/`)
- `LoginRequest` (`login-request.model.ts`):
  ```ts
  export interface LoginRequest {
    collegeId: string;
    password: string;
  }
  ```
- `LoginResponse` (`login-response.model.ts`):
  ```ts
  export interface LoginResponse {
    token: string;
  }
  ```

## 3. Pages & Components

### 3.1 `LoginPage` (`app-login-page`)
- **Files**: `src/app/features/auth/pages/login-page/login-page.{ts,html,scss}`
- **Behavior**: Wrapper page rendering a glass card and `<app-login-form>`.

### 3.2 `LoginForm` (`app-login-form`)
- **Files**: `src/app/features/auth/component/login-form/login-form.{ts,html,scss}`
- **Dependencies**: `FormBuilder`, `Auth`, `TokenService`, `Router`.
- **State**: `isLoading = false`, `errorMessage = ''`.
- **Reactive Form**:
  - `collegeId`: `['', [Validators.required, Validators.minLength(3)]]`
  - `password`: `['', [Validators.required, Validators.minLength(6)]]`
- **Role Redirect Map (`DASHBOARD_ROUTES`)**:
  - `SUPER_ADMIN` → `/dashboard/system` *(Flaw: route does not exist)*
  - `ADMIN` → `/dashboard/users`
  - `INSTRUCTOR` → `/dashboard/inventory`
  - `STUDENT` → `/dashboard/inventory`
- **Lifecycle & Methods**:
  - `ngOnInit()`: If `tokenService.isLoggedIn()`, immediately calls `redirectUser(role)`.
  - `onLogin()`: Marks form touched if invalid; calls `auth.login(formValue)`; stores token again via `tokenService.setToken(res.token)` (redundant with `Auth.login`); reads `tokenService.getRole()` and redirects. Maps `401` and `403` to user-facing error messages.

## 4. Flaws & Gaps
- `SUPER_ADMIN` redirects to `/dashboard/system`, which is not defined in `app.routes.ts`.
- Duplicate `setToken()` call in both `Auth.login()` and `LoginForm.onLogin()`.
- `Auth.login()` logs credentials and JWT payload to browser console.
- No password visibility toggle or ARIA labels.
