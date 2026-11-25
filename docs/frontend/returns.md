# Frontend Feature: Returns

## 1. Routes (`src/app/features/returns/returns.routes.ts`)
Lazy-loaded under `/dashboard/returns` (no feature-level `roleGuard`):
- `''` → `ReturnsHomePage`
- `'active'` → `ActiveCheckoutsPage`
- `'process/:requestId'` → `ProcessReturnPage`
- `'history'` → `ReturnListPage`
- `'history/:returnId'` → `ReturnsDetailsPage`

## 2. Models (`src/app/features/returns/models/`)
- **Enums** (`return-enums.model.ts`):
  - `ItemCondition`: `GOOD`, `DAMAGED`, `LOST`
  - `OverallCondition`: `INTACT`, `MINOR_DAMAGE`, `MAJOR_DAMAGE`, `PARTIAL_LOSS`, `TOTAL_LOSS` *(mismatches backend: `GOOD`, `PARTIAL_DAMAGE`, `FULL_DAMAGE`)*
  - `ReturnStatus`: `PENDING_INSPECTION`, `PROCESSED`, `FINE_PENDING` *(mismatches backend: `INITIATED`, `COMPLETED`)*
- `ProcessReturnRequestModel` / `ProcessReturnItemModel` (`process-return.model.ts`):
  ```ts
  export interface ProcessReturnItemModel {
    requestItemId: string;
    quantityReturned: number;
    condition: ItemCondition | string;
    damageRemarks?: string;
  }
  export interface ProcessReturnRequestModel {
    requestId: string;
    items: ProcessReturnItemModel[];
    remarks?: string;
  }
  ```
- `ReturnResponseModel` / `ReturnItemResponseModel` (`return-response.model.ts`):
  - `ReturnResponseModel`: `id`, `requestId`, `returnedBy`, `inspectedBy`, `status`, `overallCondition`, `fineGenerated`, `remarks`, `createdAt`, `inspectedAt`, `items`.

## 3. Service (`src/app/features/returns/services/return.service.ts`)
Base URL: `${environment.apiUrl}/returns`
- `processReturn(payload: ProcessReturnRequestModel): Observable<ReturnResponseModel>` → `POST /returns`
- `getReturnById(id: string): Observable<ReturnResponseModel>` → `GET /returns/${id}`
- `getReturns(): Observable<ReturnResponseModel[]>` → `GET /returns`
- `getReturnsByRequest(requestId: string): Observable<ReturnResponseModel[]>` → `GET /returns/request/${requestId}` *(unused)*

## 4. Pages

### 4.1 `ReturnsHomePage` (`app-returns-home-page`)
- Shows "Initiate Return" card (`/dashboard/returns/active`) for non-`STUDENT` roles, and "Return History" card (`/dashboard/returns/history`) for all roles.

### 4.2 `ActiveCheckoutsPage` (`app-active-checkouts-page`)
- Calls `RequestService.getAllRequests()` and filters strictly to `status === 'APPROVED'`.
- Client-side search on request ID or `createdBy`; links to `/dashboard/returns/process/:requestId`.

### 4.3 `ProcessReturnPage` (`app-process-return-page`)
- Loads `RequestService.getRequestById(requestId)` and initializes `returnRows` with `qtyReturned = quantityApproved`, `condition = ItemCondition.GOOD`.
- Requires `damageRemarks` when condition is `DAMAGED` or `LOST`.
- `submitReturn()` normalizes `EXCELLENT`/`FAIR` → `GOOD`, validates quantities `> 0`, calls `ReturnService.processReturn()`.

### 4.4 `ReturnListPage` (`app-return-list-page`)
- Calls `ReturnService.getReturns()`, filters by return ID, request ID, `returnedBy`, and `status`. Links to `[ret.id]`.

### 4.5 `ReturnsDetailsPage` (`app-returns-details-page`)
- Reads `:returnId`, calls `ReturnService.getReturnById(returnId)`, displays metadata, items, and link back to `/dashboard/requests/:requestId`.

## 5. Flaws & Gaps
- **Enum Mismatch**: Frontend `OverallCondition` and `ReturnStatus` enums do not match backend `return-service` enum values.
- **`ActiveCheckoutsPage` Logic**: Shows all `APPROVED` requests even if already returned; excludes `PARTIALLY_APPROVED` requests (which backend `return-service` accepts).
- **Missing Import**: `ProcessReturnPage` template uses `routerLink` without importing `RouterLink` in component `imports`.
- **No Route Guards**: Students can navigate directly to `/returns/active` or `/returns/process/:requestId`.
- **Unused `refreshTrigger$`** in `ActiveCheckoutsPage` and `ReturnListPage`, and no error UI in list/details pages.
