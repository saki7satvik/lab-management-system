# Integration Architecture

## 1. API Gateway Routing
All external client traffic enters through `api-gateway` (`http://localhost:8080`), which resolves target services via Eureka (`lb://SERVICE-NAME`).

| Route ID | Path Predicate | Target Service | Filters |
|---|---|---|---|
| `user-service` | `Path=/users/**` | `lb://USER-SERVICE` | None |
| `auth-service` | `Path=/auth/**` | `lb://USER-SERVICE` | None |
| `inventory-service` | `Path=/components/**` | `lb://INVENTORY-SERVICE` | None |
| `request-service` | `Path=/requests/**` | `lb://REQUEST-SERVICE` | None |
| `issue-service` | `Path=/issues/**` | `lb://ISSUE-SERVICE` | None |
| `return-service` | `Path=/returns/**` | `lb://RETURN-SERVICE` | None |
| `*-swagger` (5 routes) | `Path=/<service>/v3/api-docs` | `lb://<SERVICE>` | `RewritePath=/<service>/v3/api-docs, /v3/api-docs` |

Global CORS allows `http://localhost:4200` with `GET, POST, PUT, DELETE, OPTIONS`, `allowed-headers=*`, `allow-credentials=true`, and `DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_UNIQUE`.

## 2. Service-to-Service Feign Integrations

```text
[request-service] --(Feign: INVENTORY-SERVICE)--> POST /components/validate
                                              --> POST /components/availability
                                              --> POST /components/deduct

[issue-service]   --(Feign: request-service)----> GET  /requests/{id}
                                              --> PUT  /requests/{id}/mark-issued (DEAD: never called, endpoint missing in request-service)
                  --(Feign: inventory-service)--> POST /components/deduct

[return-service]  --(Feign: REQUEST-SERVICE)----> GET  /requests/{requestId}
                                              --> GET  /requests (unused)
                                              --> GET  /requests/my (unused)
                  --(Feign: INVENTORY-SERVICE)--> POST /components/add-back
```

### JWT Token Propagation
`request-service`, `issue-service`, and `return-service` each define a `FeignConfig` `@Configuration` bean (`RequestInterceptor`) that reads the `Authorization` header from `RequestContextHolder.getRequestAttributes()` and forwards it on outgoing Feign calls.

## 3. End-to-End Integration Flows

### 3.1 Login Flow
`LoginForm` (`frontend`) → `Auth.login()` → `POST http://localhost:8080/auth/login` → `api-gateway` (`RouteValidator` skips JWT check) → `user-service` `AuthController.login()` → BCrypt verify & `ACTIVE` check → returns `{ token }` → `TokenService.setToken(token)` stores in `localStorage`.

### 3.2 Request Creation & Approval Flow
1. **Create**: `CreateRequestPage` loads `GET /components`, builds cart, calls `RequestService.createRequest()` → `POST /requests` → `request-service` validates component IDs via `InventoryClient.validateComponents()` (`POST /components/validate`) → saves `Request` (`PENDING`).
2. **Approve**: `RequestDetailsPage` calls `PUT /requests/{id}/approve` directly via `HttpClient` → `request-service` checks `InventoryClient.getAvailableQuantities()` (`POST /components/availability`) → calls `InventoryClient.deductStock()` (`POST /components/deduct`) → updates `Request` (`APPROVED` / `PARTIALLY_APPROVED` / `REJECTED`).

### 3.3 Return Processing Flow
1. **Active Checkouts**: `ActiveCheckoutsPage` calls `GET /requests` and filters `status === 'APPROVED'` client-side.
2. **Process Return**: `ProcessReturnPage` calls `ReturnService.processReturn()` → `POST /returns` → `return-service` fetches request via `RequestClient.getRequestById()` (`GET /requests/{requestId}`) → validates prior returned quantities → groups `GOOD` items and calls `InventoryClient.addBackStock()` (`POST /components/add-back`) → saves `Return` and `ReturnItem` entities.

## 4. Integration Flaws & Mismatches
1. **Dead Feign Call**: `issue-service` `RequestClient.markAsIssued()` targets `PUT /requests/{id}/mark-issued`, which does not exist in `request-service` (and is never called).
2. **Missing Component Name Lookup**: `request-service` `RequestItemResponseDTO` has `componentName`, but `InventoryClient` has no endpoint to fetch component names, so `componentName` is always `null`/unset.
3. **Frontend Enum Mismatch on Returns**: `frontend` `ReturnStatus` defines `PENDING_INSPECTION`, `PROCESSED`, `FINE_PENDING`; `frontend` `OverallCondition` defines `INTACT`, `MINOR_DAMAGE`, `MAJOR_DAMAGE`, `PARTIAL_LOSS`, `TOTAL_LOSS`. Backend `return-service` defines `ReturnStatus` (`INITIATED`, `COMPLETED`) and `OverallCondition` (`GOOD`, `PARTIAL_DAMAGE`, `FULL_DAMAGE`).
4. **No `issue-service` Frontend Integration**: `/issues/**` endpoints are completely unused by the Angular application.
5. **No Compensation on Remote Failure**: `InventoryClient.deductStock` and `InventoryClient.addBackStock` execute before local DB `save()` commits without rollback compensation.
