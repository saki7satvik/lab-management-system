# API Catalog

All endpoints are exposed through `api-gateway` at `http://localhost:8080`. Unless marked **Public**, all endpoints require `Authorization: Bearer <jwt>`.

## 1. Authentication & Users (`user-service` — `8081`)

| Method | Path | Roles | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/auth/login` | Public | `{ collegeId, password }` | `200` `{ token }` |
| `POST` | `/users` | `SUPER_ADMIN`, `ADMIN` | `{ name, collegeId, email, phone, role }` | `201` `UserResponseDTO` |
| `GET` | `/users` | Authenticated | — | `200` `UserResponseDTO[]` (hierarchy-filtered) |
| `GET` | `/users/{collegeId}` | Authenticated | — | `200` `UserResponseDTO` |
| `PUT` | `/users/{collegeId}/deactivate` | `SUPER_ADMIN`, `ADMIN` | — | `200` `"User deactivated"` |
| `PUT` | `/users/{collegeId}/reactivate` | `SUPER_ADMIN`, `ADMIN` | — | `200` `UserResponseDTO` |

## 2. Inventory (`inventory-service` — `8082`)

| Method | Path | Roles | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/components` | `INSTRUCTOR` | `{ name, description, totalQuantity, comments }` | `200` `ComponentResponseDTO` |
| `PUT` | `/components/increase` | `INSTRUCTOR` | `{ componentId, quantity, comments }` | `200` `ComponentResponseDTO` |
| `PUT` | `/components/reduce` | `INSTRUCTOR` | `{ componentId, quantity, comments }` | `200` `ComponentResponseDTO` |
| `GET` | `/components` | Authenticated | — | `200` `ComponentResponseDTO[]` |
| `GET` | `/components/{id}` | Authenticated | — | `200` `ComponentResponseDTO` |
| `POST` | `/components/validate` | Authenticated | `string[]` (componentIds) | `200` `Map<String, Boolean>` |
| `POST` | `/components/availability` | Authenticated | `string[]` (componentIds) | `200` `Map<String, Integer>` |
| `POST` | `/components/deduct` | `INSTRUCTOR` | `{ items: [{ componentId, quantity }] }` | `200` `StockUpdateResponseDTO[]` |
| `POST` | `/components/add-back` | `INSTRUCTOR` | `{ items: [{ componentId, quantity }] }` | `200` `StockUpdateResponseDTO[]` |

## 3. Requests (`request-service` — `8083`)

| Method | Path | Roles | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/requests` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | `{ targetType, targetId, items: [{ componentId, quantity }] }` | `200` `RequestResponseDTO` |
| `GET` | `/requests/{id}` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `RequestResponseDTO` |
| `GET` | `/requests/my` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `RequestResponseDTO[]` |
| `GET` | `/requests/user/{collegeId}` | `ADMIN`, `INSTRUCTOR` | — | `200` `RequestResponseDTO[]` |
| `GET` | `/requests` | `ADMIN`, `INSTRUCTOR` | — | `200` `RequestResponseDTO[]` |
| `PUT` | `/requests/{requestId}/approve` | `INSTRUCTOR` | `{ items: [{ requestItemId, quantityApproved }] }` | `200` `RequestResponseDTO` |
| `PUT` | `/requests/{id}/reject` | `INSTRUCTOR` | — | `200` `RequestResponseDTO` |

## 4. Issues (`issue-service` — `8084`)

| Method | Path | Roles | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/issues` | `INSTRUCTOR` | `{ requestId, items: [{ requestItemId, quantity }] }` | `200` `IssueResponseDTO` |
| `GET` | `/issues/{id}` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `IssueResponseDTO` |
| `GET` | `/issues/request/{requestId}` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `IssueResponseDTO[]` |
| `GET` | `/issues` | `ADMIN`, `INSTRUCTOR` | — | `200` `IssueResponseDTO[]` |

## 5. Returns (`return-service` — `8085`)

| Method | Path | Roles | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/returns` | `INSTRUCTOR` | `{ requestId, items: [{ requestItemId, quantityReturned, condition, damageRemarks }], remarks }` | `200` `ReturnResponseDTO` |
| `GET` | `/returns/{id}` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `ReturnResponseDTO` |
| `GET` | `/returns` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `ReturnResponseDTO[]` |
| `GET` | `/returns/request/{requestId}` | `ADMIN`, `INSTRUCTOR`, `STUDENT` | — | `200` `ReturnResponseDTO[]` |

## 6. Standard Error Response Schema
Controller-level exceptions (`GlobalExceptionHandler`) and Spring Security entry-point/access-denied handlers return:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/requests",
  "timestamp": "2026-11-25T17:25:00Z"
}
```
*(Note: `JwtFilter` `401`/`403` errors and `api-gateway` `401` errors do not return this JSON structure.)*
