# Data Architecture

## 1. Database-per-Service Topology
Each domain microservice connects to a dedicated MySQL database on `localhost:3306` using Hibernate `ddl-auto=update` and `MySQLDialect`. There are **no cross-database foreign keys**; cross-service references are stored as plain `String` UUIDs or `collegeId` strings.

| Service | Schema Name | Tables |
|---|---|---|
| `user-service` | `lab_management_user_db` | `users` |
| `inventory-service` | `lab_management_inventory_db` | `component`, `component_history` |
| `request-service` | `lab_management_request_db` | `requests`, `request_items` |
| `issue-service` | `lab_management_issue_db` | `issues`, `issue_items` |
| `return-service` | `lab_management_return_db` | `returns`, `return_items` |

---

## 2. Entity-Relationship Diagrams & Schemas

### 2.1 `lab_management_user_db`
**Table: `users`** (`com.user_service.entity.User`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `college_id` | `String` | `@Column(unique = true, nullable = false)` |
| `name` | `String` | Nullable |
| `email` | `String` | `@Column(unique = true, nullable = false)` |
| `phone` | `String` | Nullable |
| `password` | `String` | `@Column(nullable = false)` (BCrypt hash) |
| `role` | `Role` (Enum STRING) | `SUPER_ADMIN`, `ADMIN`, `INSTRUCTOR`, `STUDENT` |
| `status` | `Status` (Enum STRING) | `ACTIVE`, `INACTIVE`, `SUSPENDED` |

---

### 2.2 `lab_management_inventory_db`
**Table: `component`** (`com.inventory_service.entity.Component`, no explicit `@Table`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `name` | `String` | `@Column(unique = true, nullable = false)` |
| `description` | `String` | Nullable |
| `total_quantity` | `int` | `@Column(nullable = false)` |
| `available_quantity` | `int` | `@Column(nullable = false)` |
| `active` | `boolean` | `@Column(nullable = false)`, default `true` |
| `created_by` | `String` | Nullable (`collegeId`) |
| `updated_by` | `String` | Nullable (`collegeId`) |
| `created_at` | `LocalDateTime` | Set in `@PrePersist` |
| `updated_at` | `LocalDateTime` | Set in `@PrePersist` and `@PreUpdate` |

**Table: `component_history`** (`com.inventory_service.entity.ComponentHistory`, no explicit `@Table`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `component_id` | `String` | `@Column(nullable = false)` (logical ref, no FK) |
| `action` | `ActionType` (Enum STRING) | `CREATE`, `INCREASE`, `REDUCE`, `DEDUCT`, `ADD_BACK` (`nullable = false`) |
| `quantity_changed` | `int` | `@Column(nullable = false)` (negative for `DEDUCT`) |
| `total_quantity_after` | `int` | `@Column(nullable = false)` |
| `available_quantity_after` | `int` | `@Column(nullable = false)` |
| `performed_by` | `String` | `@Column(nullable = false)` |
| `remarks` | `String` | Nullable |
| `timestamp` | `LocalDateTime` | Set in `@PrePersist` |

---

### 2.3 `lab_management_request_db`
Relationship: `requests` (1) ──── (*) `request_items` (FK `request_id`, `CascadeType.ALL`, `orphanRemoval = true`).

**Table: `requests`** (`com.request_service.entity.Request`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `created_by` | `String` | `@Column(nullable = false)` |
| `target_type` | `TargetType` (Enum STRING) | `INDIVIDUAL`, `GROUP` (`nullable = false`) |
| `target_id` | `String` | `@Column(nullable = false)` |
| `status` | `RequestStatus` (Enum STRING) | `PENDING`, `PARTIALLY_APPROVED`, `APPROVED`, `REJECTED` (`nullable = false`) |
| `created_at` | `LocalDateTime` | `@Column(nullable = false)`, `@PrePersist` |
| `updated_at` | `LocalDateTime` | `@Column(nullable = false)`, `@PrePersist` & `@PreUpdate` |

**Table: `request_items`** (`com.request_service.entity.RequestItem`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `request_id` | `String` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "request_id", nullable = false)` |
| `component_id` | `String` | `@Column(nullable = false)` |
| `quantity_requested` | `int` | `@Column(nullable = false)` (`> 0` validated in `@PrePersist`/`@PreUpdate`) |
| `quantity_approved` | `int` | `@Column(nullable = false)`, default `0` (`>= 0` validated in `@PrePersist`/`@PreUpdate`) |
| `status` | `RequestItemStatus` (Enum STRING) | `PENDING`, `APPROVED`, `PARTIAL`, `REJECTED` (`nullable = false`) |

---

### 2.4 `lab_management_issue_db`
Relationship: `issues` (1) ──── (*) `issue_items` (FK `issue_id`, `CascadeType.ALL`, `orphanRemoval = true`).

**Table: `issues`** (`com.issue_service.entity.Issue`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `request_id` | `String` | `@Column(nullable = false)` |
| `issued_by` | `String` | `@Column(nullable = false)` |
| `issue_date` | `LocalDateTime` | `@Column(nullable = false)`, overwritten in `@PrePersist` |
| `status` | `IssueStatus` (Enum STRING) | `ISSUED`, `PARTIALLY_ISSUED` (`nullable = false`) |

**Table: `issue_items`** (`com.issue_service.entity.IssueItem`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `issue_id` | `String` | `@ManyToOne`, `@JoinColumn(name = "issue_id", nullable = false)` |
| `request_item_id` | `String` | `@Column(nullable = false)` |
| `component_id` | `String` | `@Column(nullable = false)` |
| `quantity_issued` | `int` | `@Column(nullable = false)` |

---

### 2.5 `lab_management_return_db`
Relationship: `returns` (1) ──── (*) `return_items` (FK `return_id`, `CascadeType.ALL`, `orphanRemoval = true`).

**Table: `returns`** (`com.return_service.entity.Return`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `request_id` | `String` | `@Column(nullable = false)` |
| `returned_by` | `String` | `@Column(nullable = false)` |
| `inspected_by` | `String` | Nullable |
| `status` | `ReturnStatus` (Enum STRING) | `INITIATED`, `COMPLETED` (`nullable = false`; **overwritten to `INITIATED` in `@PrePersist`**) |
| `overall_condition` | `OverallCondition` (Enum STRING) | `GOOD`, `PARTIAL_DAMAGE`, `FULL_DAMAGE` (Nullable, never populated) |
| `fine_generated` | `boolean` | `@Column(nullable = false)` (**overwritten to `false` in `@PrePersist`**) |
| `remarks` | `String` | Nullable |
| `created_at` | `LocalDateTime` | Set in `@PrePersist` |
| `inspected_at` | `LocalDateTime` | Nullable (never populated) |

**Table: `return_items`** (`com.return_service.entity.ReturnItem`)
| Column | Java Type | Constraints |
|---|---|---|
| `id` | `String` | `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)` |
| `return_id` | `String` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "return_id", nullable = false)` |
| `request_item_id` | `String` | `@Column(nullable = false)` |
| `component_id` | `String` | `@Column(nullable = false)` |
| `quantity_returned` | `int` | `@Column(nullable = false)` |
| `item_condition` | `ItemCondition` (Enum STRING) | `GOOD`, `DAMAGED`, `LOST` (`@Column(name = "item_condition")`) |
| `damage_remarks` | `String` | Nullable |
| `created_at` | `LocalDateTime` | Set in `@PrePersist` |

## 3. Data Architecture Flaws
- `Return.prePersist()` unconditionally resets `status = ReturnStatus.INITIATED` and `fineGenerated = false`, overwriting `COMPLETED` and `true` set by `ReturnServiceImpl`.
- `IssueItem` lacks an explicit `@NoArgsConstructor` (relies on Lombok `@Builder` package-private constructor, which may fail JPA requirements in some proxies).
- No `@Version` optimistic locking on any entity.
- No Flyway/Liquibase migrations; relies entirely on `spring.jpa.hibernate.ddl-auto=update`.
- No database indexes on foreign-reference string columns (`request_id`, `component_id`, `created_by`, `returned_by`).
