# Frontend Layouts & Shared Components

## 1. Layouts

### 1.1 `AuthLayout`
- **Files**: `src/app/layouts/auth-layout/auth-layout.{ts,html,scss}`
- **Template**: `<div class="auth-container"><router-outlet></router-outlet></div>`
- **Styling**: `min-height: 100vh`, flexbox centered, `padding: 2rem`.
- **Notes**: Contains an unused `inject` import in `auth-layout.ts`.

### 1.2 `DashboardLayout`
- **Files**: `src/app/layouts/dashboard-layout/dashboard-layout.{ts,html,scss}`
- **State**: `isSidebarOpen = false`
- **Methods**: `toggleSidebar(): void` (toggles `isSidebarOpen`)
- **Structure**:
  ```html
  <div class="dashboard-container">
    <app-sidebar [isOpen]="isSidebarOpen" (closeSidebar)="isSidebarOpen = false"></app-sidebar>
    <main class="main-content">
      <header class="topbar">
        <button class="burger-btn" (click)="toggleSidebar()">...</button>
        <span>Lab Management System</span>
      </header>
      <section class="page-body">
        <router-outlet></router-outlet>
      </section>
    </main>
  </div>
  ```
- **Responsive Behavior**: On desktop (`> 768px`), sidebar is fixed/visible and `.burger-btn` is hidden. On mobile (`<= 768px`), sidebar translates off-screen until `.open` is applied. No backdrop overlay or focus trap exists when open.

---

## 2. Shared Components (`src/app/shared/components/`)

### 2.1 `Sidebar` (`app-sidebar`)
- **Files**: `sidebar/sidebar.{ts,html,scss}`
- **Inputs/Outputs**: `@Input() isOpen = false`, `@Output() closeSidebar = new EventEmitter<void>()`
- **Behavior**:
  - On `ngOnInit()`, reads `TokenService.getRole()` and selects `MENU_CONFIG[role ?? 'STUDENT']`.
  - Renders navigation links using `@for` with `routerLink` and `routerLinkActive="active"`.
  - Embeds `<app-profile-menu>`.
  - `onCloseClick()` emits `closeSidebar`.
- **Role Menu Configuration** (`src/app/core/constants/menu.config.ts`):
  | Role | Menu Items (Label → Route) |
  |---|---|
  | `SUPER_ADMIN` | Users → `/dashboard/users`, Inventory → `/dashboard/inventory`, Requests → `/dashboard/requests`, Returns → `/dashboard/returns` |
  | `ADMIN` | Users → `/dashboard/users`, Inventory → `/dashboard/inventory` |
  | `INSTRUCTOR` | Users → `/dashboard/users`, Inventory → `/dashboard/inventory`, Requests → `/dashboard/requests`, Returns → `/dashboard/returns` |
  | `STUDENT` | Components → `/dashboard/inventory`, My Requests → `/dashboard/requests`, My Returns → `/dashboard/returns` |

### 2.2 `ProfileMenu` (`app-profile-menu`)
- **Files**: `profile-menu/profile-menu.{ts,html,scss}`
- **State**: `collegeId = ''`, `role = ''`, `isDropdownOpen = false`
- **Behavior**:
  - `ngOnInit()` populates `collegeId` (`TokenService.getCollegeId()`) and `role` (`TokenService.getRole()`).
  - `toggleDropdown()` toggles `isDropdownOpen`.
  - `onLogout()` calls `Auth.logout()` and navigates to `/login`.
  - **Gap**: No click-outside or `Escape` key listener to close the dropdown.

### 2.3 `FeatureCardComponent` (`app-feature-card`)
- **File**: `feature-card/feature-card.ts` (inline template & styles)
- **Inputs**: `title = ''`, `description = ''`, `icon = ''`, `link = ''`, `iconColor = 'var(--primary-cyan)'`
- **Behavior**: Renders a clickable `.glass-panel` card with `[routerLink]="link"`.

### 2.4 `PageHeaderComponent` (`app-page-header`)
- **File**: `page-header/page-header.ts` (inline template & styles)
- **Inputs**: `title = ''`, `backRoute?: string`
- **Behavior**: Displays page title, optional back link (`[routerLink]="backRoute"`), and `<ng-content></ng-content>` slot for action buttons.
