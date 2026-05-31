# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean install
mvn clean package -DskipTests

# Run locally
mvn spring-boot:run

# Test
mvn test
mvn test -Dtest=ClassName          # single test class
mvn test -Dtest=ClassName#method   # single test method

# Docker
docker build -t cctv-core-app:latest .
```

## Architecture Overview

Spring Boot 3.5 microservice for CCTV camera management. Java 21, Maven. Server runs on port `8080` with context path `/cctv-core`.

**Layered structure:** `web/` → `service/` → `repository/` → `model/`

### Key Packages

- **`web/`** — REST controllers: cameras (CRUD, CSV import), analytics, notifications, user info
- **`service/`** — Business logic. `IStorageService` has two implementations: `LocalStorage` and `R2Storage` (Cloudflare R2). `CCTVSSEService` manages Server-Sent Events for live camera status. `StreamService` delegates to go2rtc via Feign.
- **`repository/`** — Spring Data MongoDB repositories for `CCTVCameraInfo`, `CCTVUserInfo`, `Notification`
- **`model/`** — MongoDB documents. `BaseEntity` provides audit fields. `CCTVCameraInfo` has geospatial indexing (GEO_2DSPHERE) on location.
- **`event/`** — Redis pub/sub. `IntrusionEventSubscriber` listens on `intrusion_alerts`, stores to MongoDB, broadcasts to WebSocket `/topic/intrusion-alerts`, and uploads snapshot. `ModifyCCTVPublisher` sends zone changes on `modify_zone_cctv`.
- **`client/`** — Feign clients: `Go2rtcClient` (stream management, snapshots) and `IdpClient` (Keycloak user lookup)
- **`scheduler/`** — `CCTVHealthCheck` polls camera health at a configurable interval (default 60s), updates status, broadcasts via SSE
- **`mapper/`** — MapStruct: `CCTVInfoMapper`, `CCTVUserInfoMapper`, `CSVMapper`
- **`security/`** — `CustomAuthoritiesConverter` extracts roles from JWT; `@ConfiguratorAccess` and `@AdminAccess` are method-level security annotations

### Real-Time Communication

Three channels coexist:
1. **SSE** — `GET /cameras/status/stream` pushes camera status updates to browser clients
2. **WebSocket (STOMP)** — `/ws` endpoint, topic `/topic/intrusion-alerts` for intrusion alerts; JWT authenticated via `JwtChannelInterceptor`
3. **Redis pub/sub** — internal event bus between application components

### Response Format

All API responses are wrapped: `AppResponse<T>` with `code` (1000 = success), `message`, and `data`. Use `ApiResponseUtil` helpers for construction. Custom exceptions use `AppException` + `ErrorCode` enum.

### Infrastructure Dependencies

| Service | Purpose | Default |
|---|---|---|
| MongoDB | Primary storage | `localhost:27017` |
| Redis | Pub/sub event bus | `localhost:6379` |
| Keycloak | OAuth2/JWT (realm: `smart-cctv`) | `localhost:8081` |
| go2rtc | RTSP stream management | `localhost:1984` |
| Cloudflare R2 | Cloud snapshot storage (optional) | — |

Configuration is in `application.yaml` with environment variable overrides. See `.env.example` for required variables.

### Annotation Processing Order

MapStruct depends on Lombok, so the Maven compiler plugin configures Lombok first, then MapStruct as annotation processors. Keep this order in `pom.xml` if adding processors.
