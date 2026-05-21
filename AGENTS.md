# AGENTS.md

## Overview

TAK (Tjänsteadresseringskatalogen) is a Swedish healthcare service addressing catalog — part of the SKLTP (Tjänsteplattform) platform. It resolves which service producer should handle a given service contract for a logical address.

## Architecture

**Multi-module Maven project** (Java 17, Spring Boot 3.5):

| Module            | Role                                                              | Parent POM               |
|-------------------|-------------------------------------------------------------------|--------------------------|
| `tak-core`        | JPA entities, DAOs, facades — shared domain model                 | `tak-parent`             |
| `tak-schemas`     | WSDL/XSD contract definitions                                     | `tak-parent`             |
| `tak-services`    | SOAP/REST services (CXF) exposing TAK data + cache reset endpoint | `tak-spring-boot-parent` |
| `tak-web`         | Admin GUI (Spring MVC + Thymeleaf) for managing TAK entries       | `tak-spring-boot-parent` |
| `tak-monitor`     | Polls DB version and triggers `tak-services` cache resets         | `tak-spring-boot-parent` |
| `tak-integration` | CronJob for TAK data export                                       | `tak-parent`             |

**Two-level POM hierarchy**: `tak-parent` (root) → `tak-spring-boot-parent` (adds Boot web/actuator deps) → deployable modules.

### Key domain entities (`tak-core/src/.../entity/`)
`Vagval`, `Anropsbehorighet`, `Tjanstekontrakt`, `Tjanstekomponent`, `LogiskAdress`, `AnropsAdress`, `RivTaProfil`, `Filter`, `Filtercategorization`, `PubVersion`

### Inter-component communication
- `tak-monitor` polls the DB for new `PubVersion` entries and triggers cache resets on multiple downstream services via REST calls:
  - `tak-services` → `/tak-services/reset/pv`
  - VP (Virtualisering Platform) → `/resetcache`
  - KAT → `/kat/resetcache`
  - AGP → `/resetcache`
  - EI-backend → `/skltp-ei/resetcache`
- In Kubernetes, `tak-monitor` uses the K8s API (`io.kubernetes:client-java`) to discover pod IPs by label (`usePodLookup=true`), then calls each pod directly.
- In Docker Compose / local dev, static URLs are configured via `tak.monitor.reset.nodes[].url`.

## Build & Run

```powershell
# Full build (from tak/ root)
mvn clean package

# Run with Docker Compose (MySQL + all services)
docker compose up --build

# Local ports: tak-web=8001, tak-services=8002, MySQL=3306, phpMyAdmin=8081

# Code coverage report
mvn clean verify -Ptest-coverage

# License header check/update
mvn license:check -Plicense
mvn license:format -Plicense
```

Spring profile `dev` is active by default when running via `spring-boot-maven-plugin`. Production containers use `-Dspring.profiles.active=production`.

## Deployment (Helm + ArgoCD)

- `helm/` — Helm chart for TAK itself (deployments, services, ingress, configmaps).
- The ArgoCD `app-of-apps` configuration lives in a **separate repository** — not in this workspace. It deploys all SKLTP services (TAK, VP, EI, etc.) to Kubernetes.

Container images: `docker.drift.inera.se/ntjp/tak-{web,services,monitor,integration}`.  
Actuator health on port `8089`; application traffic on `8080`. Prometheus metrics exposed at `/actuator/prometheus`.

## Conventions

- **Dependency versions**: ALL versions declared in root `pom.xml` `<dependencyManagement>` or as `<properties>`. Child modules NEVER declare versions.
- **License headers**: Every `.java` file must carry the LGPL-2.1 header. Run `mvn license:format -Plicense` to auto-fix.
- **Logging**: Log4j2 with ECS JSON layout (`co.elastic.logging:log4j2-ecs-layout`). Never use `System.out`.
- **Tests**: JUnit 5 + Mockito. H2 in-memory DB used in `tak-core` tests. DBUnit for fixture data.
- **Helm value layering**: `_default_config_maps` in chart defaults + environment-specific `config_maps`/`secrets` injected via ArgoCD valuefiles.
- **tak-web code generation**: OpenAPI Generator produces a Feign client from `tak-web/src/main/resources/aaa-spec.yaml` (AAA service integration). MapStruct handles entity↔DTO mapping.
- **Security**: `tak-web` uses Spring Security with forward-auth support and JDBC-backed sessions (`spring-session-jdbc`). Profile `forwardauth` activates header-based authentication; profile `setSecureSessions` enforces secure session cookies.

## File Patterns

| Pattern                                    | Purpose                                        |
|--------------------------------------------|------------------------------------------------|
| `tak-*/src/main/java/se/skltp/tak/**`      | Application source                             |
| `tak-*/src/test/java/**/*Test.java`        | Unit tests                                     |
| `helm/templates/*.yaml`                    | K8s manifest templates                         |
| `helm/values.yaml`                         | Helm chart defaults                            |
| `resources/sql/`                           | DB schema & seed scripts                       |
| `Jenkins.properties`                       | CI build metadata (SonarQube key, JDK, branch) |
| `tak-web/src/main/resources/aaa-spec.yaml` | OpenAPI spec for AAA client generation         |

