---
description: Package structure and layered architecture of the cctv-core service
alwaysApply: true
---

Spring Boot 3.5 microservice, Java 21, Maven. Context path `/cctv-core`, port `8080`.

**Layered flow:** `web/` → `service/` → `repository/` → `model/`

## Key Packages

- **`web/`** — REST controllers: cameras (CRUD, CSV import), analytics, notifications, user info
- **`service/`** — Business logic. `IStorageService` has two implementations: `LocalStorage` and `R2Storage` (Cloudflare R2). `CCTVSSEService` manages SSE for live camera status. `StreamService` delegates to go2rtc via Feign.
- **`repository/`** — Spring Data MongoDB repositories for `CCTVCameraInfo`, `CCTVUserInfo`, `Notification`
- **`model/`** — MongoDB documents. `BaseEntity` provides audit fields (created_at, updated_at, created_by, updated_by). `CCTVCameraInfo` has geospatial indexing (GEO_2DSPHERE) on location.
- **`event/`** — Redis pub/sub handlers (see `realtime-events` rule)
- **`client/`** — Feign clients: `Go2rtcClient` (stream management, snapshots) and `IdpClient` (Keycloak user lookup)
- **`scheduler/`** — `CCTVHealthCheck` polls camera health on a configurable interval (default 60s), updates status, broadcasts via SSE
- **`mapper/`** — MapStruct mappers: `CCTVInfoMapper`, `CCTVUserInfoMapper`, `CSVMapper`
- **`security/`** — `CustomAuthoritiesConverter` extracts roles from JWT; `@ConfiguratorAccess` and `@AdminAccess` are method-level security annotations

## Annotation Processing Order

Lombok must be declared before MapStruct in the Maven compiler plugin. Preserve this order in `pom.xml` when adding new annotation processors.
