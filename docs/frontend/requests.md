# Frontend Feature: Requests

## 1. Routes (`src/app/features/requests/requests.routes.ts`)
Lazy-loaded under `/dashboard/requests` (no feature-level `roleGuard`):
- `''` → `RequestListPage`
- `'create'` → `CreateRequestPage`
- `':id'` → `RequestDetailsPage`

## 2. Models (`src/app/features/requests/models/`)
- `CreateRequestModel` (`request/create-request.model.ts`): `{ targetType: String; targetId: String; items: RequestItemModel[]; }`
- `RequestItemModel` (`request/request-item.model.ts`): `{ componentId: String; quantity: number; }`
- `ApproveRequestItemModel` (`request/approve-request-item.model.ts`): `{ requestItemId: string; quantityApproved: number; }` *(unused)*
- `RequestItemResponseModel` (`response/request-item-response.model.ts`): `{ id: string; componentId: string; componentName: string; quantityRequested: number; quantityApproved: number; status: string; }`
- `RequestResponseModel` (`response/request-response.model.ts`): `{ id: string; createdBy: string; targetType: string; targetId: string; status: string; createdAt: Date; updatedAt: Date; items: RequestItemResponseModel[]; }`

## 3. Service (`src/app/features/requests/services/request.service.ts`)
Base URL: `environment.apiUrl`
- `createRequest(requestData: CreateRequestModel): Observable<RequestResponseModel>` → `POST ${API}/requests`
- `getAllRequests(): Observable<RequestResponseModel[]>` → `GET ${API}/requests`
- `getRequestById(id: string): Observable<RequestResponseModel>` → `GET ${API}/requests/${id}`
- `getMyRequests(): Observable<RequestResponseModel[]>` → `GET ${API}/requests/my`
- `getRequestsByUserId(collegeId: string): Observable<RequestResponseModel[]>` → `GET ${API}/requests/user/${collegeId}`

## 4. Pages

### 4.1 `RequestListPage` (`app-request-list-page`)
- Loads `getMyRequests()` if `userRole === 'STUDENT'`, otherwise `getAllRequests()`.
- Client-side filters by request ID, `createdBy`, and `status`.
- Shows "New Request" button only for `STUDENT`.

### 4.2 `CreateRequestPage` (`app-create-request-page`)
- Loads `InventoryService.getAllComponents()`, filters out `availableQuantity <= 0`.
- Client-side search by component name; manages local `cart: CartItem[]` (`addToCart`, `removeFromCart`).
- `submitRequest()` builds `{ targetType: 'INDIVIDUAL', targetId: tokenService.getCollegeId() ?? 'UNKNOWN', items }` and calls `RequestService.createRequest()`.

### 4.3 `RequestDetailsPage` (`app-request-details-page`)
- Reads `:id` from `ActivatedRoute`, calls `RequestService.getRequestById(id)`.
- Maps items to `EditableItem` (`editableApprovedQty`).
- For `INSTRUCTOR` when `status === 'PENDING'`:
  - `processApproval()` injects `HttpClient` directly to call `PUT ${API}/requests/${request.id}/approve` with `{ items: [{ requestItemId, quantityApproved }] }`.
  - `processRejection()` injects `HttpClient` directly to call `PUT ${API}/requests/${request.id}/reject` with `{}`.

## 5. Flaws & Gaps
- `RequestDetailsPage` bypasses `RequestService` by injecting `HttpClient` directly for approve/reject and uses nested `.subscribe()` calls.
- `ApproveRequestItemModel` is unused.
- `CreateRequestPage` uses `'UNKNOWN'` fallback for `targetId` and only supports `'INDIVIDUAL'` (no UI for `'GROUP'`).
- Input models use boxed `String` instead of `string`; `createdAt`/`updatedAt` are typed as `Date` despite JSON strings.
- No `roleGuard` on `/create` or `/:id`; no HTTP error/loading UI on list/details.
