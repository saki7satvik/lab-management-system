# Service: `return-service`

## 1. Overview & Configuration
- **Module Path**: `backend/return-service/return-service/`
- **Artifact**: `com:return-service:0.0.1-SNAPSHOT` (Spring Boot `3.5.14`, Spring Cloud `2025.0.2`, Java 17)
- **Port**: `8085` (`spring.application.name=return-service`)
- **Database**: `jdbc:mysql://localhost:3306/lab_management_return_db` (`ddl-auto=update`)
- **OpenFeign Clients**: `RequestClient` (`REQUEST-SERVICE`), `InventoryClient` (`INVENTORY-SERVICE`), propagating `Authorization` via `FeignConfig`.

## 2. Data Model (`com.return_service`)
- **Entity `Return`** (`returns`):
  - `id` (`String`, UUID), `requestId` (`String`, not null), `returnedBy` (`String`, not null), `inspectedBy` (`String`), `status` (`ReturnStatus`: `INITIATED`, `COMPLETED`), `overallCondition` (`OverallCondition`: `GOOD`, `PARTIAL_DAMAGE`, `FULL_DAMAGE`), `fineGenerated` (`boolean`, not null), `remarks` (`String`), `createdAt` (`LocalDateTime`), `inspectedAt` (`LocalDateTime`), `items` (`@OneToMany(mappedBy = "returnEntity", cascade = ALL, orphanRemoval = true)`).
  - `@PrePersist` sets `createdAt = LocalDateTime.now()`, `status = ReturnStatus.INITIATED`, `fineGenerated = false`.
- **Entity `ReturnItem`** (`return_items`):
  - `id` (`String`, UUID), `returnEntity` (`@ManyToOne(LAZY)` `return_id`), `requestItemId` (`String`, not null), `componentId` (`String`, not null), `quantityReturned` (`int`, not null), `condition` (`ItemCondition`: `GOOD`, `DAMAGED`, `LOST`, column `item_condition`), `damageRemarks` (`String`), `createdAt` (`LocalDateTime`, `@PrePersist`).
- **Repository `ReturnRepository`**:
  - `findByReturnedBy(String)`, `findByRequestId(String)`, `getTotalReturnedQuantity(String)` *(JPQL `SUM` returning `int`, unused)*.

## 3. API Endpoints (`ReturnController` — `/returns`)
All successful responses return `200 OK` with `ReturnResponseDTO` (or `List<ReturnResponseDTO>`).

- `POST /returns` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid ProcessReturnRequestDTO` (`requestId` `@NotBlank`, `items` `@NotEmpty @Valid` of `ProcessReturnItemDTO { requestItemId, quantityReturned > 0, condition, damageRemarks }`, `remarks`).
  - Logic: Active `INSTRUCTOR` check; max 50 items, no duplicate `requestItemId`; fetches request via `RequestClient.getRequestById()`; requires `APPROVED` or `PARTIALLY_APPROVED`; sums prior returned quantities from `findByRequestId`; requires `damageRemarks` for `DAMAGED`/`LOST`; rejects if `alreadyReturned + qty > quantityApproved`; sets `returnedBy = requestData.createdBy`, `inspectedBy = currentUser.id`; groups `GOOD` items by `componentId` and calls `InventoryClient.addBackStock()` (`POST /components/add-back`); sets `fineGenerated = true` if any item is non-`GOOD`; saves `Return`.
- `GET /returns/{id}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: `ADMIN`/`INSTRUCTOR` any; `STUDENT` only if `returnedBy.equals(currentUser.id)`.
- `GET /returns` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: `STUDENT` → `findByReturnedBy(currentUser.id)`; `ADMIN`/`INSTRUCTOR` → `findAll()`.
- `GET /returns/request/{requestId}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: Calls `RequestClient.getRequestById()`; `STUDENT` must match `request.createdBy`; returns `findByRequestId(requestId)`.

## 4. Flaws & Gaps
- **Critical `@PrePersist` Overwrite Bug**: `Return.prePersist()` unconditionally overwrites `status` to `ReturnStatus.INITIATED` and `fineGenerated` to `false`, discarding `COMPLETED` and `fineGenerated = true` set by `ReturnServiceImpl`.
- **Dead Inspection Workflow**: `InspectReturnRequestDTO` and `InspectReturnItemDTO` exist, `overallCondition` and `inspectedAt` exist on `Return`, but no inspection endpoint exists.
- **No Fine Service**: `fineGenerated` is only a boolean flag; no fine calculation or service integration exists.
- **Non-atomic Remote Add-Back**: `InventoryClient.addBackStock()` executes before `returnRepository.save()`.
- **Minimal Tests**: Only `ReturnServiceApplicationTests.contextLoads()` exists.
