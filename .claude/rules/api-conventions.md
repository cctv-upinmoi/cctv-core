---
description: REST API response format, error handling, and security annotations
globs: src/main/java/**/web/**/*.java, src/main/java/**/service/**/*.java, src/main/java/**/exception/**/*.java
alwaysApply: false
---

## Response Format

All API responses use `AppResponse<T>`:
- `code` — `1000` means success; use `ErrorCode` enum for all other codes
- `message` — human-readable status
- `data` — payload, can be null

Use `ApiResponseUtil` helpers to build responses — never construct `AppResponse` inline.

## Error Handling

Throw `AppException` with an `ErrorCode` value for all expected error conditions. Do not use raw `RuntimeException`. The `ErrorCode` enum is the single source of truth for error codes.

## Security

- `@AdminAccess` — restricts endpoint to ADMIN role
- `@ConfiguratorAccess` — restricts endpoint to CONFIGURATOR role
- Apply at method level on controller endpoints, not at class level unless all methods share the same restriction.
- JWT role extraction is handled by `CustomAuthoritiesConverter`; do not duplicate role-checking logic in service layer.
