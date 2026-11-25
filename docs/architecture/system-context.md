# System Context

## 1. Purpose & Domain
The **Lab Management System** is a distributed web application for managing college laboratory hardware/components, user accounts, component borrow requests, component issuance, and component returns.

## 2. Actors & Roles
Roles are defined across the system (`Role` enum):
- **`SUPER_ADMIN`**: Top-level administrator. Bootstrapped on `user-service` startup (`SUPER_ADMIN_001`). Can manage `ADMIN`, `INSTRUCTOR`, and `STUDENT` accounts.
- **`ADMIN`**: Laboratory administrator. Can manage `INSTRUCTOR` and `STUDENT` accounts, view requests, issues, and returns.
- **`INSTRUCTOR`**: Laboratory staff/faculty. Can manage inventory components and stock, approve/reject requests, issue components, process returns, and create `STUDENT` accounts.
- **`STUDENT`**: End user. Can view active inventory components, submit borrow requests, and view their own requests, issues, and returns.

User account statuses (`Status` enum): `ACTIVE`, `INACTIVE`, `SUSPENDED`.

## 3. High-Level System Topology

```text
+-----------------------+        HTTP/REST (JWT)       +-------------------------+
|   Angular Frontend    | ---------------------------> |      API Gateway        |
|  (localhost:4200)     |                              |   (localhost:8080)      |
+-----------------------+                              +-------------------------+
                                                                   |
                                            Service Discovery      | Load-Balanced
                                            (lb://SERVICE-NAME)    | Routing
                                                                   v
+-----------------------+     Registers / Fetches      +-------------------------+
|     Eureka Server     | <--------------------------> |  Domain Microservices   |
|  (localhost:8761)     |                              |                         |
+-----------------------+                              | - user-service (8081)   |
                                                       | - inventory-service(8082)|
                                                       | - request-service (8083)|
                                                       | - issue-service (8084)  |
                                                       | - return-service (8085) |
                                                       +-------------------------+
                                                                   |
                                                                   v
                                                       +-------------------------+
                                                       |   MySQL 8 Databases     |
                                                       |   (localhost:3306)      |
                                                       |   1 DB per service      |
                                                       +-------------------------+
```

## 4. Service Inventory & Ports

| Component | Technology | Port | Database |
|---|---|---|---|
| Frontend SPA | Angular 21 (`@angular/build:application`) | `4200` | N/A |
| `eureka-server` | Spring Boot 3.5.13 / Spring Cloud Netflix Eureka | `8761` | N/A |
| `api-gateway` | Spring Boot 3.5.13 / Spring Cloud Gateway (WebFlux) | `8080` | N/A |
| `user-service` | Spring Boot 3.5.13 / Spring Data JPA / Security | `8081` | `lab_management_user_db` |
| `inventory-service` | Spring Boot 3.5.13 / Spring Data JPA / Security | `8082` | `lab_management_inventory_db` |
| `request-service` | Spring Boot 3.5.13 / Spring Data JPA / OpenFeign | `8083` | `lab_management_request_db` |
| `issue-service` | Spring Boot 3.5.14 / Spring Data JPA / OpenFeign | `8084` | `lab_management_issue_db` |
| `return-service` | Spring Boot 3.5.14 / Spring Data JPA / OpenFeign | `8085` | `lab_management_return_db` |

## 5. Core Business Workflows
1. **Authentication & User Provisioning**:
   - `user-service` seeds `SUPER_ADMIN_001` (`superadmin@lab.com`, password `welcome@123`).
   - Higher-privilege users create lower-privilege users (`SUPER_ADMIN` → `ADMIN`/`INSTRUCTOR`/`STUDENT`, `ADMIN` → `INSTRUCTOR`/`STUDENT`, `INSTRUCTOR` → `STUDENT`) with default password `welcome@123`.
   - Users authenticate via `POST /auth/login` and receive an HS256 JWT (1-hour expiry) containing `sub` (collegeId), `role`, and `status`.
2. **Inventory Management**:
   - `INSTRUCTOR` creates components (`POST /components`) and adjusts stock (`PUT /components/increase`, `PUT /components/reduce`), generating `ComponentHistory` audit records.
   - All authenticated users can browse active components (`GET /components`, `GET /components/{id}`).
3. **Request Lifecycle**:
   - `STUDENT`, `INSTRUCTOR`, or `ADMIN` submits a request (`POST /requests`) with target type (`INDIVIDUAL` or `GROUP`) and component items (max 50). `request-service` validates component existence via `inventory-service`.
   - `INSTRUCTOR` approves (`PUT /requests/{requestId}/approve`)—which checks availability and deducts stock in `inventory-service`—or rejects (`PUT /requests/{id}/reject`).
4. **Issuance Lifecycle** *(Backend implemented; no frontend UI)*:
   - `INSTRUCTOR` issues approved items (`POST /issues`) via `issue-service`, which validates the request in `request-service` and deducts stock in `inventory-service` again.
5. **Return Lifecycle**:
   - `INSTRUCTOR` processes returns (`POST /returns`) for `APPROVED` or `PARTIALLY_APPROVED` requests.
   - Items marked `GOOD` are added back to `inventory-service` (`POST /components/add-back`). Items marked `DAMAGED` or `LOST` require `damageRemarks` and set `fineGenerated = true`.
