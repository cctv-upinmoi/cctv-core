---
description: Infrastructure dependencies, service defaults, and environment configuration
globs: src/main/resources/**, .env*, docker-compose*, Dockerfile
alwaysApply: false
---

## Service Dependencies

| Service | Purpose | Default |
|---|---|---|
| MongoDB | Primary storage | `localhost:27017` |
| Redis | Internal event bus (pub/sub, legacy) | `localhost:6379` |
| RabbitMQ | Internal event bus (pub/sub, default) | `localhost:5672` |
| Keycloak | OAuth2/JWT, realm `smart-cctv` | `localhost:8081` |
| go2rtc | RTSP stream management | `localhost:1984` |
| Cloudflare R2 | Cloud snapshot storage (optional) | — |

## Configuration

- All settings live in `src/main/resources/application.yaml` with environment variable overrides.
- `.env.example` lists all required environment variables for local development — copy it to `.env` to get started.
- Storage is selectable: set the storage provider to `local` or `r2` via config. R2 requires Cloudflare credentials in env vars.
- Camera health check interval defaults to `60s`; override via `app.camera.health-check-interval`.
