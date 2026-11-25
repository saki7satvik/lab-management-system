# Service: `inventory-service`

## 1. Overview & Configuration
- **Module Path**: `backend/inventory_service/inventory_service/`
- **Artifact**: `com:inventory_service:0.0.1-SNAPSHOT` (Spring Boot `3.5.13`, Spring Cloud `2025.0.2`, Java 17)
- **Port**: `8082` (`spring.application.name=inventory-service`)
- **Database**: `jdbc:mysql://localhost:3306/lab_management_inventory_db` (`ddl-auto=update`)
- **Log File**: `logs/inventory-service.log`

## 2. Data Model (`com.inventory_service`)
- **Entity `Component`** (table `component`):
  - `id` (`String`, UUID `@Id`), `name` (`String`, unique, not null), `description` (`String`), `totalQuantity` (`int`, not null), `availableQuantity` (`int`, not null), `active` (`boolean`, default `true`, not null), `createdBy` (`String`), `updatedBy` (`String`), `createdAt` (`LocalDateTime`, `@PrePersist`), `updatedAt` (`LocalDateTime`, `@PrePersist`/`@PreUpdate`).
- **Entity `ComponentHistory`** (table `component_history`):
  - `id` (`String`, UUID `@Id`), `componentId` (`String`, not null), `action` (`ActionType`: `CREATE`, `INCREASE`, `REDUCE`, `DEDUCT`, `ADD_BACK`), `quantityChanged` (`int`, negative for `DEDUCT`), `totalQuantityAfter` (`int`), `availableQuantityAfter` (`int`), `performedBy` (`String`), `remarks` (`String`), `timestamp` (`LocalDateTime`, `@PrePersist`).
- **Repositories**:
  - `InventoryRepository`: `existsByName(String)`, `findAllById(Iterable<String>)`, `findByIdAndActiveTrue(String)`.
  - `ComponentHistoryRepository`: standard `JpaRepository`.

## 3. API Endpoints (`InventoryController` — `/components`)
All successful responses return `200 OK`.
- `POST /components` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid CreateComponentDTO` (`name` `@NotBlank`, `description`, `totalQuantity` `@Min(1)`, `comments` unused).
  - Returns: `ComponentResponseDTO` (`id`, `name`, `description`, `totalQuantity`, `availableQuantity`).
  - Logic: Trims name, checks duplicate (`409`), sets `availableQuantity = totalQuantity`, logs `CREATE` history.
- `PUT /components/increase` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid UpdateStockDTO` (`componentId` `@NotBlank`, `quantity` `@Min(1)`, `comments` unused).
  - Logic: Adds `quantity` to both `totalQuantity` and `availableQuantity`, logs `INCREASE` history.
- `PUT /components/reduce` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid UpdateStockDTO`.
  - Logic: Rejects (`409`) if `totalQuantity < qty` or `availableQuantity < qty`; subtracts from both, logs `REDUCE` history.
- `GET /components` — Authenticated (any role)
  - Returns: `List<ComponentResponseDTO>` (filters `Component::isActive` in memory).
- `GET /components/{id}` — Authenticated (any role)
  - Returns: `ComponentResponseDTO` (`404` if missing or inactive).
- `POST /components/validate` — Authenticated (any role; OpenAPI marks unauthenticated in docs only)
  - Body: `List<String>` componentIds → Returns `Map<String, Boolean>`.
- `POST /components/availability` — Authenticated (any role; OpenAPI marks unauthenticated in docs only)
  - Body: `List<String>` componentIds → Returns `Map<String, Integer>` (0 if missing/inactive).
- `POST /components/deduct` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid BulkStockUpdateDTO` (`items` `@NotEmpty` of `StockUpdateItemDTO { componentId, quantity }`).
  - Transactional: Deducts `availableQuantity` only (`totalQuantity` unchanged), logs `DEDUCT` history (negative `quantityChanged`). Returns `List<StockUpdateResponseDTO>`.
- `POST /components/add-back` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid BulkStockUpdateDTO`.
  - Transactional: Adds to `availableQuantity`, rejects (`409`) if `availableQuantity + qty > totalQuantity`, logs `ADD_BACK` history.

## 4. Flaws & Gaps
- `BulkStockUpdateDTO.items` lacks `@Valid`, so nested `StockUpdateItemDTO` constraints are not enforced.
- No `@Version` optimistic locking or DB-level constraints on stock quantities; concurrent deductions/reductions can race.
- `CreateComponentDTO.comments` and `UpdateStockDTO.comments` are silently ignored.
- No endpoint to view `ComponentHistory` or deactivate/delete components.
- `OpenApiConfig` clears security in Swagger docs for `validateComponents` and `getAvailableQuantities`, but `AppConfig` still requires JWT at runtime.
