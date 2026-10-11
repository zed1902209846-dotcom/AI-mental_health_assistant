# AGENTS.md

AI psychological-assistant. All backend code lives in `backend/` (run every Maven command from there); the repo root is reserved for a future frontend. Spring Boot 3.5.15 / Java 17, Maven wrapper (`./mvnw`), base package `com.example.aispringboot`.

## Run / verify
- Run: `./mvnw spring-boot:run`
- Test: `./mvnw test` — the only test is `contextLoads`, which needs a live MySQL and AI API key or it fails. Don't rely on it as a fast loop.
- No lint/format/typecheck step beyond the compiler; verify changes with `./mvnw -q compile`.

## Config (easy to miss)
- `backend/src/main/resources/application.yml` is gitignored. **It does not exist in the repo.** To run locally you must `cp application.yml.example application.yml`.
- External secrets come from env vars: `DB_USERNAME`, `DB_PASSWORD`, `AI_API_KEY`, `JWT_SECRET`.
- Requires a MySQL DB named `mental_health_assistant`; there is **no schema.sql / Flyway / Liquibase** — tables must exist already.
- AI provider is OpenAI-compatible pointing at SiliconFlow (`base-url: https://api.siliconflow.cn`, model `deepseek-ai/DeepSeek-V3`). Server port 1236.

## Stack
- MyBatis-Plus 3.5.7 (`mybatis-plus-spring-boot3-starter`). Mappers are `@Mapper` interfaces extending `BaseMapper` in `mapper/`. Entities use Lombok `@Data/@Builder` and map snake_case columns with `@TableField`.
- Spring Security + Auth0 `java-jwt` 4.4.0. `util/JwtTokenUtil` is **all-static** and reaches Spring beans via `ApplicationContextAware`, so no bean injection needed at call sites.
- JWT is read from the HTTP header **`token`** (in `extractTokenFromRequest`), not `Authorization`. `JwtConfig.header`/`tokenPrefix` are declared but unused by the filter.
- Auth flow: `SecurityConfig.PUBLIC_PATHS` are whitelisted (`/`, `/api/text`, `/api/user/login`, `/api/user/add`); `JwtAuthenticationFilter` skips those and enforces JWT elsewhere.

## Architecture
- `controller/` thin REST layer → `service/` (DB ops) → `mapper/`. AI logic lives separately in `AiServices/` (e.g. `PsychologicalSupportService`).
- Streaming chat (`POST /api/psychological-chat/stream`) returns Reactor `Flux<ServerSentEvent<String>>` (produces `text/plain`), not a typical JSON response.
- Chat memory: `MessageWindowChatMemory` (30 msgs) + `MessageChatMemoryAdvisor`. The memory conversationId is `"conversation_" + sessionId` where sessionId is `"session_" + <db row id>`; `PsychologicalSupportService.streamPsychologicalChat` derives the DB id from the session string.
- `ChatClientConfig` defines the `"open-ai"` `ChatClient` bean — inject by `@Qualifier("open-ai")`.

## Conventions
- Global API envelope is `common/Result`; errors go through `GlobalExceptionHandler`. Domain errors throw `exception/BusinessException`.
- Comment style in code is Chinese; keep new comments matching (project's own convention, not a hard rule).
