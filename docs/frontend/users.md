# Frontend Feature: Users

## 1. Routes (`src/app/features/users/users.routes.ts`)
Lazy-loaded under `/dashboard/users` (no feature-level `roleGuard`):
- `''` → `UsersHomePage`
- `'list'` → `UserListPage`
- `'create'` → `CreateUserPage`

## 2. Models (`src/app/features/users/models/`)
- `CreateUserRequest` (`create-user-request.model.ts`):
  ```ts
  export interface CreateUserRequest {
    name: string;
    collegeId: string;
    email: string;
    phone: string;
    role: string;
  }
  ```
- `UserResponse` (`user-response.model.ts`):
  ```ts
  export interface UserResponse {
    name: string;
    collegeId: string;
    email: string;
    phone: string;
    role: string;
    status: string;
  }
  ```

## 3. Service (`src/app/features/users/services/user.service.ts`)
Base URL: `environment.apiUrl`
- `createUser(user: CreateUserRequest): Observable<UserResponse>` → `POST ${API}/users`
- `getAllUsers(): Observable<UserResponse[]>` → `GET ${API}/users`
- `getUserById(collegeId: string): Observable<UserResponse>` → `GET ${API}/users/${collegeId}`
- `deactivateUser(collegeId: string): Observable<void>` → `PUT ${API}/users/${collegeId}/deactivate` (body `{}`)
- `reactivateUser(collegeId: string): Observable<void>` → `PUT ${API}/users/${collegeId}/reactivate` (body `{}`)

## 4. Pages & Components

### 4.1 `UsersHomePage` (`app-users-home-page`)
- Reads `currentUserRole` from `TokenService`.
- Renders `FeatureCardComponent` for Directory (`/dashboard/users/list`) and Provision Account (`/dashboard/users/create`, visible only for `SUPER_ADMIN` or `ADMIN`).

### 4.2 `UserListPage` (`app-user-list-page`)
- **State**: `currentUserRole`, `refreshTrigger$` (`BehaviorSubject<void>`), `searchQuery$`, `selectedRole$`, `selectedStatus$`, `rawUsers$` (`switchMap` + `shareReplay(1)`), `filteredUsers$` (`combineLatest`).
- **Filtering**: Client-side filter on `name`, `collegeId`, `email`, `role`, and `status`.
- **Actions**: `onDeactivate(collegeId)` and `onReactivate(collegeId)` use browser `confirm()` and `alert()`, then call `refreshData()`.

### 4.3 `CreateUserPage` (`app-create-user-page`)
- **Reactive Form**:
  - `name`: `required`
  - `collegeId`: `required`
  - `email`: `required`, `email`
  - `phone`: `pattern('^[0-9]{10}$')` (not required)
  - `role`: `required`
- **Role Options**: `ADMIN` (only shown if current user is `SUPER_ADMIN`), `INSTRUCTOR`, `STUDENT`.
- **Submit**: Calls `UserService.createUser()`, navigates to `/dashboard/users` on success, shows generic `errorMessage` on failure.

### 4.4 Unused Placeholder Components
- `UserTable` (`user-table/`), `UserForm` (`user-form/`), `UserActions` (`user-actions/`): Empty scaffolded components (`*-works!`).

## 5. Flaws & Gaps
- No `roleGuard` on `/dashboard/users/create` or `/list`.
- `CreateUserPage.getRole()` manually decodes JWT with `atob` without try/catch instead of using `TokenService`.
- Uses browser `confirm()`/`alert()`; HTTP errors terminate `filteredUsers$` without retry UI.
- `UserTable`, `UserForm`, and `UserActions` are dead placeholder components.
