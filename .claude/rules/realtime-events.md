---
description: Real-time communication patterns: SSE, WebSocket/STOMP, and RabbitMQ pub/sub
globs: src/main/java/**/event/**/*.java, src/main/java/**/config/RabbitMQConfig.java, src/main/java/**/config/RedisConfig.java, src/main/java/**/config/WebSocketConfig.java, src/main/java/**/service/CCTVSSEService.java
alwaysApply: false
---

Three channels coexist for real-time communication — do not conflate them:

| Channel | Direction | Use case |
|---|---|---|
| **SSE** `GET /cameras/status/stream` | Server → browser | Camera health status updates |
| **WebSocket (STOMP)** `/ws`, topic `/topic/intrusion-alerts` | Server → browser | Intrusion detection alerts |
| **RabbitMQ** (internal, default) | Service → service | Event distribution between app components |

## Messaging Provider

Controlled by `MESSAGING_PROVIDER` env var (default: `rabbitmq`). Both providers implement the same interfaces:
- `IModifyCCTVPublisher` — `ModifyCCTVPublisher` (Redis) / `RabbitMQModifyCCTVPublisher` (RabbitMQ)
- Intrusion listener — `IntrusionEventSubscriber` (Redis) / `RabbitMQIntrusionEventSubscriber` (RabbitMQ)

## RabbitMQ Topology (default)

Exchange: `cctv.exchange` (Topic exchange, durable)

| Queue | Routing Key | Producer | Consumer |
|---|---|---|---|
| `intrusion_alerts` | `intrusion.alerts` | AI service | `RabbitMQIntrusionEventSubscriber` |
| `modify_zone_cctv` | `modify.zone.cctv` | `RabbitMQModifyCCTVPublisher` | AI service |

## Rules

- New internal events between components must go through the messaging provider (RabbitMQ by default), not direct method calls.
- Add new event types by implementing `IModifyCCTVPublisher` or similar interfaces with both Redis and RabbitMQ implementations using `@ConditionalOnProperty`.
- WebSocket connections are JWT-authenticated via `JwtChannelInterceptor` — any new STOMP endpoint must register through `WebSocketConfig` and respect this interceptor.
- SSE streams are managed by `CCTVSSEService`; add new event types there rather than opening new SSE endpoints.
