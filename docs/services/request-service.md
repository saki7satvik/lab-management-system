# Service: `request-service`

## 1. Overview & Configuration
- **Module Path**: `backend/request_service/request_service/`
- **Artifact**: `com:request_service:0.0.1-SNAPSHOT` (Spring Boot `3.5.13`, Spring Cloud `2025.0.2`, Java 17)
- **Port**: `8083` (`spring.application.name=request-service`)
- **Database**: `jdbc:mysql://localhost:3306/lab_management_request_db` (`ddl-auto=update`)
- **OpenFeign Client**: `InventoryClient` (`@FeignClient(name = "INVENTORY-SERVICE")`), propagating `Authorization` via `FeignConfig`.

## 2. Data Model (`com.request_service`)
- **Entity `Request`** (`requests`):
  - `id` (`String`, UUID), `createdBy` (`String`, not null), `targetType` (`TargetType`: `INDIVIDUAL`, `GROUP`), `targetId` (`String`, not null), `status` (`RequestStatus`: `PENDING`, `PARTIALLY_APPROVED`, `APPROVED`, `REJECTED`), `createdAt`, `updatedAt`, `items` (`@OneToMany(mappedBy = "request", cascade = ALL, orphanRemoval = true)`).
  - `@PrePersist` sets `createdAt`, `updatedAt`, and defaults `status = PENDING` if null.
- **Entity `RequestItem`** (`request_items`):
  - `id` (`String`, UUID), `request` (`@ManyToOne(LAZY)` `request_id`), `componentId` (`String`, not null), `quantityRequested` (`int`, `> 0`), `quantityApproved` (`int`, `>= 0`, default `0`), `status` (`RequestItemStatus`: `PENDING`, `APPROVED`, `PARTIAL`, `REJECTED`).
- **Repository `RequestRepository`**: `findByCreatedBy(String createdBy)`.

## 3. API Endpoints (`RequestController` — `/requests`)
All successful endpoints return `200 OK` with `RequestResponseDTO` (or `List<RequestResponseDTO>`).

- `POST /requests` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Body: `@Valid CreateRequestDTO` (`targetType` `@NotNull`, `targetId` `@NotNull`, `items` `@NotEmpty` of `RequestItemDTO { componentId, quantity >= 1 }`).
  - Logic: Active user check; if `INDIVIDUAL`, requires `targetId.equals(currentUser.id)` (`GROUP` membership not validated); max 50 items, no duplicate `componentId`; calls `InventoryClient.validateComponents()` (`POST /components/validate`); saves `Request` (`PENDING`) and `RequestItem`s (`PENDING`).
- `GET /requests/{id}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: Creator or `ADMIN`/`INSTRUCTOR` only.
- `GET /requests/my` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: Returns `findByCreatedBy(currentUser.id)`.
- `GET /requests/user/{collegeId}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")`
  - Logic: Returns `findByCreatedBy(collegeId)`.
- `GET /requests` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")`
  - Logic: Returns `findAll()`.
- `PUT /requests/{requestId}/approve` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid ApproveRequestDTO` (`items` `@NotEmpty` of `ApproveRequestItemDTO { requestItemId, quantityApproved >= 0 }`).
  - Logic: Requires `RequestStatus.PENDING`; max 50 items, no duplicates; calls `InventoryClient.getAvailableQuantities()` (`POST /components/availability`); checks `0 <= approved <= requested` and `approved <= available`; calls `InventoryClient.deductStock()` (`POST /components/deduct`) for items with `approved > 0`; updates item statuses (`APPROVED`, `PARTIAL`, `REJECTED`) and overall status (`APPROVED`, `PARTIALLY_APPROVED`, `REJECTED`).
- `PUT /requests/{id}/reject` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Logic: Requires `PENDING`; sets all items `quantityApproved = 0`, `status = REJECTED`, request `status = REJECTED`.

## 4. Flaws & Gaps
- **Non-atomic Remote Deduction**: `InventoryClient.deductStock()` is called before `requestRepository.save()` without Saga compensation if DB save fails.
- **Unpopulated `componentName`**: `dto.response.RequestItemResponseDTO` includes `componentName`, but `InventoryClient` has no endpoint to fetch component names.
- **No `GROUP` Validation**: `TargetType.GROUP` skips `targetId` validation.
- **No Concurrency Locking**: Two instructors can concurrently approve the same `PENDING` request.
- **Duplicate DTOs & Dead Code**: `dto.request` has duplicate `RequestResponseDTO`/`RequestItemResponseDTO` classes, plus unused `BulkAddBackRequestDTO`/`BulkDeductStockRequestDTO`.
- **Broken Controller Test**: `RequestControllerTests` calls `/requests/user` without `{collegeId}`.
