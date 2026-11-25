# Project Documentation & Reconstruction Standards

## Mission & Context
This repository currently contains only `backend/` and `frontend/` source code with **no existing documentation**.
- **Primary Objective:** Reverse-engineer the codebase to generate a complete, implementation-accurate documentation suite under `/docs`.
- **Reconstruction Standard:** Documentation is a first-class artifact. Every file must contain enough architectural, behavioral, and schema detail that a developer or AI agent can reconstruct the entire application from `/docs` alone without reading the original source code.

---

## Codebase Scanning & Generation Workflow

When asked to generate, initialize, or audit documentation, execute in this exact order:

1. **Inspect Project Root & Infrastructure:** Scan build files (`pom.xml`, `package.json`, `angular.json`, `docker-compose.yml`, `Dockerfile`, `.env*`, application properties/YAML) to map tech stack, ports, dependencies, and environment configurations.
2. **Analyze Backend (`backend/`):** Scan each microservice or module (controllers, DTOs, entities/models, repositories, services, security filters, messaging/Feign/HTTP clients, exception handlers, and SQL/migration scripts).
3. **Analyze Frontend (`frontend/`):** Scan routing modules, layouts, guards, interceptors, state management, shared/core services, and feature modules/components.
4. **Cross-Reference Integrations:** Trace end-to-end flows from UI actions → frontend services → API gateway/endpoints → backend services → database schemas.
5. **Generate `/docs`:** Create the exact directory hierarchy and files defined below. Never leave `TODO` placeholders—derive actual values, endpoints, payloads, and logic directly from the source code.

---

## Documentation Directory Structure

Generate and maintain all documentation strictly under `/docs` using this exact hierarchy:

```text
docs/
├── architecture/
│   ├── system-context.md
│   ├── frontend-architecture.md
│   ├── backend-architecture.md
│   ├── data-architecture.md
│   ├── integration-architecture.md
│   └── deployment-architecture.md
├── frontend/
│   ├── frontend-overview.md
│   ├── layouts.md
│   ├── core-services.md
│   └── <feature-name>.md          # One file per frontend feature/module
├── services/
│   └── <service-name>.md          # One file per backend microservice/module
├── api/
│   └── api-catalog.md
└── security/
    └── security-architecture.md