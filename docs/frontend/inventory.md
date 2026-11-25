# Frontend Feature: Inventory

## 1. Routes (`src/app/features/inventory/inventory.routes.ts`)
Lazy-loaded under `/dashboard/inventory` (no feature-level `roleGuard`):
- `''` → `InventoryHomePage`
- `'list'` → `ComponentListPage`
- `'create'` → `CreateComponentPage`

## 2. Models (`src/app/features/inventory/models/`)
- `CreateComponentModel` (`create-component.model.ts`):
  ```ts
  export interface CreateComponentModel {
    name: string;
    description: string;
    totalQuantity?: number;
    comments?: string;
  }
  ```
- `ComponentResponseModel` (`component-response.model.ts`):
  ```ts
  export interface ComponentResponseModel {
    id: string;
    name: string;
    description: string;
    totalQuantity: number;
    availableQuantity: number;
  }
  ```

## 3. Service (`src/app/features/inventory/services/inventory.service.ts`)
Base URL: `environment.apiUrl` (injects `TokenService` but does not use it):
- `createComponent(data: CreateComponentModel): Observable<ComponentResponseModel>` → `POST ${API}/components`
- `getAllComponents(): Observable<ComponentResponseModel[]>` → `GET ${API}/components`
- `getComponentById(id: string): Observable<ComponentResponseModel>` → `GET ${API}/components/${id}`

## 4. Pages & Components

### 4.1 `InventoryHomePage` (`app-inventory-home-page`)
- Displays cards for View Directory (`/dashboard/inventory/list`) and Provision Component (`/dashboard/inventory/create`, shown only for `INSTRUCTOR`).

### 4.2 `ComponentListPage` (`app-component-list-page`)
- **State**: `refreshTrigger$`, `searchQuery$`, `rawComponents$` (`shareReplay(1)`), `filteredComponents$`.
- **Filtering**: Client-side filter by `name` and `description`.
- **Template**: Displays table with `id`, `name`, `description`, `totalQuantity`, `availableQuantity`, and hardcoded stock status badges (`0` → Out of Stock, `1-3` → Critical, `>3` → Stable).

### 4.3 `CreateComponentPage` (`app-create-component-page`)
- **Reactive Form**: `name` (`required`), `description` (no validator), `totalQuantity` (`required`, `min(1)`, default `0`), `comments` (no validator).
- **Submit**: Calls `InventoryService.createComponent()`, navigates to `/dashboard/inventory` on success.

### 4.4 Unused Placeholder Pages & Components
- `StockHistoryPage` (`pages/stock-history-page/`), `ComponentFilters` (`components/component-filters/`), `ComponentTable` (`components/component-table/`), `StockUpdateDialog` (`components/stock-update-dialog/`): Empty scaffolded components.

## 5. Flaws & Gaps
- No `roleGuard` on `/dashboard/inventory/create`.
- No frontend UI or service methods for `PUT /components/increase`, `PUT /components/reduce`, or stock history.
- `CreateComponentPage` submits `comments`, which the backend `InventoryServiceImpl` ignores.
- `ComponentListPage` has `refreshTrigger$`/`refreshData()` unused in template, no pagination, and no HTTP error handling.
- 4 placeholder components (`StockHistoryPage`, `ComponentFilters`, `ComponentTable`, `StockUpdateDialog`) are dead code.
