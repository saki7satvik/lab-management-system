# Service: `issue-service`

## 1. Overview & Configuration
- **Module Path**: `backend/issue-service/issue-service/`
- **Artifact**: `com:issue-service:0.0.1-SNAPSHOT` (Spring Boot `3.5.14`, Spring Cloud `2025.0.2`, Java 17)
- **Port**: `8084` (`spring.application.name=issue-service`)
- **Database**: `jdbc:mysql://localhost:3306/lab_management_issue_db` (`ddl-auto=update`)
- **OpenFeign Clients**: `RequestClient` (`request-service`), `InventoryClient` (`inventory-service`), propagating `Authorization` via `FeignConfig`.

## 2. Data Model (`com.issue_service`)
- **Entity `Issue`** (`issues`):
  - `id` (`String`, UUID), `requestId` (`String`, not null), `issuedBy` (`String`, not null), `issueDate` (`LocalDateTime`, not null, overwritten in `@PrePersist`), `status` (`IssueStatus`: `ISSUED`, `PARTIALLY_ISSUED`, not null), `items` (`@OneToMany(mappedBy = "issue", cascade = ALL, orphanRemoval = true)`).
- **Entity `IssueItem`** (`issue_items`):
  - `id` (`String`, UUID), `issue` (`@ManyToOne` `issue_id`), `requestItemId` (`String`, not null), `componentId` (`String`, not null), `quantityIssued` (`int`, not null).
- **Repository `IssueRepository`**: `findByRequestId(String)`, `findByRequestIdIn(List<String>)` *(unused)*.

## 3. API Endpoints (`IssueController` — `/issues`)
All successful responses return `200 OK` with `IssueResponseDTO` (`issueId`, `requestId`, `issuedBy`, `status`, `issueDate`, `items: [{ issueItemId, componentId, quantityIssued }]`).

- `POST /issues` — `@PreAuthorize("hasRole('INSTRUCTOR')")`
  - Body: `@Valid CreateIssueDTO` (`requestId` `@NotNull`, `items` `@NotEmpty` of `IssueItemDTO { requestItemId, quantity >= 1 }`).
  - Logic: Active `INSTRUCTOR` check; rejects duplicate `requestItemId`; fetches request via `RequestClient.getRequestById()`; requires `APPROVED` or `PARTIALLY_APPROVED`; sums prior `IssueItem` quantities per `requestItemId` and rejects if `alreadyIssued + qty > quantityApproved`; calls `InventoryClient.deductStock()` (`POST /components/deduct`); sets `ISSUED` if all submitted items reach `quantityApproved`, else `PARTIALLY_ISSUED`; saves `Issue`.
- `GET /issues/{id}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: `ADMIN`/`INSTRUCTOR` can view any; `STUDENT` must match `request.createdBy` (fetched via `RequestClient`).
- `GET /issues/request/{requestId}` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")`
  - Logic: Queries `findByRequestId(requestId)` (returns `[]` if none, does not 404). For `STUDENT`, checks if any returned issue's request has `createdBy == currentUser.id`; if so, returns all issues for that request.
- `GET /issues` — `@PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")`
  - Logic: Returns `findAll()`.

## 4. Flaws & Gaps
- **Double Stock Deduction**: `request-service` already deducts stock on `approve`, and `issue-service` deducts stock *again* on `issueComponents()`.
- **Unused `RequestClient.markAsIssued()`**: Declared in `RequestClient` (`PUT /requests/{id}/mark-issued`), never called, and no such endpoint exists in `request-service`.
- **Typo in Package Name**: `com.issue_service.secutity` (instead of `security`).
- **Partial Submission Status Bug**: `IssueStatus.ISSUED` is computed only over submitted `items`, ignoring unsubmitted approved items on the request.
- **`IssueItem` Missing `@NoArgsConstructor`**: May cause JPA proxy instantiation issues.
- **No Frontend Integration**: No Angular feature consumes `/issues/**`.
- **Non-atomic Remote Deduction & Concurrency**: Deducts inventory before DB save; no locking against concurrent issuance.
