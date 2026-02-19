# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Withus** is a Spring Boot REST API backend for a couples mobile app (iOS + Android). It handles authentication, couple management, daily questions, photo uploads, and push notifications.

- **Stack:** Java 21, Spring Boot 3.5.9, MySQL, Redis, AWS S3, Firebase FCM
- **Auth:** JWT + OAuth 2.0 (Kakao, Apple)

## Common Commands

```bash
# Build (skip tests)
./gradlew build -x test

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.herethere.withus.SomeTest"

# Run locally with dev profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Docker Compose (full stack)
docker-compose up -d
```

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` when running locally.

## Architecture

Layered architecture: **Controller → Service → Repository**

All source code lives under `com.herethere.withus`. Domain modules:

| Module | Responsibility |
|---|---|
| `auth` | OAuth login (Kakao/Apple), JWT issue/refresh/revoke |
| `user` | User profile, onboarding, invite codes |
| `couple` | Couple creation, joining via invite code, couple keywords |
| `question` | Daily couple questions, photo uploads per question |
| `keyword` | Relationship keywords, daily keyword records |
| `fourcut` | Four-cut photo upload and gallery (cursor pagination) |
| `memory` | Custom & weekly memories (photo timeline) |
| `archive` | Calendar view aggregating questions and memories |
| `notification` | FCM push notifications |
| `s3` | Presigned URL generation for direct client-to-S3 uploads |
| `scheduling` | Daily cron job (midnight KST) assigning new questions |
| `deeplink` | Thymeleaf landing page for iOS/Android deep link invitations |
| `common` | JWT, security, exception handling, AOP logging, cursor types, shared config |

### Infrastructure Flow

```
Mobile App → Nginx (TLS) → Spring Boot :8080 → MySQL (JPA)
                                              → Redis (token caching)
                                              → AWS S3 (images)
                                              → Firebase FCM (push, async)
                                              → Kakao/Apple APIs (OAuth via Feign)
```

### Key Patterns

- **JWT filter:** `JwtAuthenticationFilter` is a custom filter inserted before Spring Security's default filter chain. Sessions are stateless.
- **OAuth factory:** `OAuthClientFactory` dispatches to `KakaoClient` or `AppleClient` (Feign clients) based on the provider string.
- **Async FCM notifications:** `CoupleNotificationEvent` is published inside transactions and consumed by `FcmNotificationEventListener` using `@TransactionalEventListener(phase = AFTER_COMMIT)` on a dedicated `fcmExecutor` thread pool.
- **Cursor-based pagination:** `CreatedAtIdCursor`, `DateCursor`, `NumberCursor` are used for infinite-scroll APIs across multiple domains.
- **S3 presigned URL pattern:** Client requests a presigned URL from `/api/s3/...`, uploads the file directly to S3, then sends the returned image key back to the backend in subsequent requests.
- **Timezone:** `Asia/Seoul` is set globally at application startup via `@PostConstruct`.

## Configuration

| File | Purpose |
|---|---|
| `src/main/resources/application.yaml` | Main config: datasource, Redis, JWT, OAuth keys, Feign, SpringDoc |
| `src/main/resources/application-dev.yaml` | Dev profile: log level overrides, rolling log file |
| `docker-compose.yml` | Orchestrates: app, Redis, Nginx, Certbot |
| `nginx.conf` | TLS termination, HTTP→HTTPS redirect, deep link static files, proxy to app |
| `firebase-key.json` | Firebase service account key (mounted read-only in container) |
| `apple-app-site-association` | iOS universal links config (served as static) |
| `assetlinks.json` | Android App Links config (served as static) |

### Required Environment Variables

```
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
OAUTH_KAKAO_APP_ADMINKEY
OAUTH_APPLE_APP_KEYID
OAUTH_APPLE_APP_TEAMID
OAUTH_APPLE_APP_ID
OAUTH_APPLE_APP_PRIVATEKEY
```

AWS S3 credentials and Firebase key are also required (key mounted as a file at `/config/firebase-key.json` in Docker).