# Deployment Architecture

## 1. Current Environment & Tooling
- **Containerization / Orchestration**: None. There are no `Dockerfile`, `docker-compose.yml`, or Kubernetes manifests in the repository.
- **Database**: Local MySQL instance expected on `localhost:3306` with 5 pre-created databases (`lab_management_user_db`, `lab_management_inventory_db`, `lab_management_request_db`, `lab_management_issue_db`, `lab_management_return_db`), username `root`, and password configured in each service's `application.properties` (currently masked/local).
- **Runtime Prerequisites**:
  - Java 17 JDK
  - Maven (or included `./mvnw` wrapper in each service directory)
  - Node.js & `npm@11.0.0` (for Angular CLI `21.2.9`)
  - MySQL 8.x

## 2. Startup Order & Local Execution

Services must be started in dependency order:

1. **MySQL Server** on port `3306` with all 5 databases created.
2. **Eureka Server** (`port 8761`):
   ```bash
   cd backend/eureka/eureka && ./mvnw spring-boot:run
   ```
3. **API Gateway** (`port 8080`):
   ```bash
   cd backend/api_gateway/api_gateway && ./mvnw spring-boot:run
   ```
4. **Domain Microservices** (any order after Eureka is up):
   - `user-service` (`port 8081`): `cd backend/user_service/user_service && ./mvnw spring-boot:run`
   - `inventory-service` (`port 8082`): `cd backend/inventory_service/inventory_service && ./mvnw spring-boot:run`
   - `request-service` (`port 8083`): `cd backend/request_service/request_service && ./mvnw spring-boot:run`
   - `issue-service` (`port 8084`): `cd backend/issue-service/issue-service && ./mvnw spring-boot:run`
   - `return-service` (`port 8085`): `cd backend/return-service/return-service && ./mvnw spring-boot:run`
5. **Angular Frontend** (`port 4200`):
   ```bash
   cd frontend && npm install && npm start
   ```

## 3. Configuration & Observability
- **Service Discovery**: All services register with `http://localhost:8761/eureka`.
- **Logging**: Each domain service writes to `logs/<service-name>.log` with a 10MB rolling policy and 7-day history.
- **Actuator**: `spring-boot-starter-actuator` is included in all 5 domain services, though no custom `management.endpoints` properties are configured (defaults to `/actuator/health` and `/actuator/info`).
- **Swagger UI**: Aggregated at `http://localhost:8080/swagger-ui.html` via `api-gateway` (`springdoc-openapi-starter-webflux-ui` 2.3.0).

## 4. Deployment Gaps
- No containerization (`Dockerfile` / `docker-compose.yml`).
- Hardcoded `localhost` URLs in `application.properties` and `environment.ts`.
- Placeholder `https://your-production-api.com` in `frontend/src/environments/environment.prod.ts`.
- Hardcoded JWT secret and DB credentials instead of environment variables / Spring Cloud Config.
- `backend/issue-service/issue-service/.metadata/` contains committed Eclipse IDE workspace files.
- `logs/` and `target/` build artifacts exist in the workspace.
