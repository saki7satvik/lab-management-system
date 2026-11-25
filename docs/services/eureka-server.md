# Service: `eureka-server`

## 1. Overview & Configuration
- **Module Path**: `backend/eureka/eureka/`
- **Artifact**: `com:eureka:0.0.1-SNAPSHOT` (Spring Boot `3.5.13`, Spring Cloud `2025.0.2`, Java 17)
- **Dependencies**: `spring-boot-starter-web`, `spring-cloud-starter-netflix-eureka-server`, `spring-boot-starter-test`
- **Main Class**: `com.eureka.EurekaApplication` annotated with `@EnableEurekaServer` and `@SpringBootApplication`.

### `application.properties`
```properties
spring.application.name=eureka-server
server.port = 8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

## 2. Behavior
- Acts as the standalone Netflix Eureka Service Registry on port `8761`.
- Does not register itself or fetch registry from peers.
- All 6 other backend services (`api-gateway`, `user-service`, `inventory-service`, `request-service`, `issue-service`, `return-service`) register with `http://localhost:8761/eureka`.

## 3. Tests
- `EurekaApplicationTests`: Basic `@SpringBootTest` `contextLoads()`.
