# InterviewIQ AI — API Design

Base path: `/api/v1`. All request/response bodies are JSON. Full contract is
generated as OpenAPI/Swagger from the Spring Boot code in Phase 2 — this doc
is the design source of truth that generation follows, not a duplicate of it.

## 1. Conventions

- **Auth**: `Authorization: Bearer <access_token>` on every endpoint except
  `auth/*` public routes. Access token is short-lived (15 min); refresh via
  `POST /auth/refresh` using the httpOnly refresh-token cookie.
- **Pagination**: cursor-free offset pagination for admin/list screens —
  `?page=0&size=20&sort=createdAt,desc`. Response envelope:
  ```json
  { "content": [...], "page": 0, "size": 20, "totalElements": 132, "totalPages": 7 }
  ```
- **Filtering/search**: query params scoped per resource, e.g.
  `GET /interviews?status=COMPLETED&role=java-backend&from=2026-01-01`.
- **Errors**: RFC 7807 problem-detail shape on every non-2xx response:
  ```json
  { "type": "VALIDATION_ERROR", "title": "Invalid request", "status": 400,
    "detail": "difficulty must be one of EASY, MEDIUM, HARD",
    "instance": "/api/v1/interviews", "traceId": "..." }
  ```
- **Status codes**: `200` read/update ok, `201` created (with `Location`
  header), `202` accepted (async AI job started), `204` deleted, `400`
  validation, `401` unauthenticated, `403` unauthorized, `404` not found,
  `409` conflict (e.g. duplicate email), `422` semantically invalid,
  `429` rate limited, `500` unhandled.
- **Idempotency**: mutating AI-triggering endpoints (e.g. resume analysis)
  accept an `Idempotency-Key` header to avoid double-billing on client retry.

## 2. Auth (`/auth`)

| Method | Path | Description |
|---|---|---|
| POST | `/auth/register` | Email + password registration, triggers verification email |
| POST | `/auth/login` | Email + password login → access + refresh token |
| POST | `/auth/oauth/google` | Exchange Google id_token for InterviewIQ session |
| POST | `/auth/refresh` | Rotate refresh token, issue new access token |
| POST | `/auth/logout` | Revoke current refresh token |
| POST | `/auth/logout-all` | Revoke all sessions for the user |
| POST | `/auth/verify-email` | Consume email verification token |
| POST | `/auth/resend-verification` | Resend verification email |
| POST | `/auth/forgot-password` | Send password reset email |
| POST | `/auth/reset-password` | Consume reset token, set new password |
| POST | `/auth/2fa/enable` | Generate TOTP secret + QR |
| POST | `/auth/2fa/verify` | Confirm TOTP code, activate 2FA |
| GET | `/auth/sessions` | List active refresh-token sessions (device, IP, last used) |

## 3. User & profile (`/users`)

| Method | Path | Description |
|---|---|---|
| GET | `/users/me` | Current user + profile + preferences |
| PATCH | `/users/me/profile` | Update profile fields |
| PATCH | `/users/me/preferences` | Theme, language, notification toggles |
| GET | `/users/{id}/public-profile` | Public profile (if `is_public`) |
| DELETE | `/users/me` | Soft-delete account |

## 4. Resume (`/resumes`)

| Method | Path | Description |
|---|---|---|
| POST | `/resumes` | Multipart upload (PDF), returns `202` + resume id, parsing runs async |
| GET | `/resumes` | List user's resumes |
| GET | `/resumes/{id}` | Parsed resume detail (skills, experience, education, projects) |
| GET | `/resumes/{id}/analysis` | AI analysis: score, ATS score, missing skills, suggestions |
| POST | `/resumes/{id}/reanalyze` | Re-run AI analysis (e.g. after manual edits) |
| PATCH | `/resumes/{id}/activate` | Mark as the active resume used for interview context |
| DELETE | `/resumes/{id}` | Remove resume |

Parsing is async: upload returns immediately, frontend polls
`GET /resumes/{id}` (status field) or receives a `RESUME_PARSED` WebSocket/
notification event.

## 5. Interview setup & lifecycle (`/interviews`)

| Method | Path | Description |
|---|---|---|
| GET | `/interviews/roles` | Available target roles (taxonomy) |
| GET | `/interviews/companies` | Available target companies (taxonomy) |
| POST | `/interviews` | Create session: role, company, difficulty, type, duration, language → `201` + session id |
| GET | `/interviews` | Paginated history, filterable by status/role/date |
| GET | `/interviews/{id}` | Session detail incl. transcript turns |
| POST | `/interviews/{id}/start` | Transition PENDING → IN_PROGRESS, starts timer server-side |
| POST | `/interviews/{id}/abandon` | Mark ABANDONED |
| GET | `/interviews/{id}/report` | Final `InterviewReport` (only after COMPLETED) |
| GET | `/interviews/{id}/report/pdf` | Signed URL / stream of generated PDF |

### 5.1 Live session protocol — WebSocket `/ws/interviews/{sessionId}`

Authenticated via JWT in the connection handshake query param. JSON message
frames, discriminated by `type`:

**Server → client**
```json
{ "type": "QUESTION", "turnId": "...", "sequenceNo": 3, "questionText": "...", "topic": "..." }
{ "type": "FOLLOW_UP", "turnId": "...", "questionText": "..." }
{ "type": "EVALUATION", "turnId": "...", "scores": { "technicalAccuracy": 78, "...": "..." }, "feedback": "..." }
{ "type": "SESSION_COMPLETE", "reportId": "..." }
{ "type": "ERROR", "code": "AI_TIMEOUT", "message": "..." }
```

**Client → server**
```json
{ "type": "ANSWER", "turnId": "...", "answerText": "...", "answerMode": "VOICE|TEXT",
  "responseTimeMs": 4200, "fillerWordCount": 3, "speakingWpm": 132 }
{ "type": "REQUEST_HINT" }
{ "type": "END_SESSION" }
```

Voice input is transcribed client-side (Web Speech API / streaming STT)
before being sent as `ANSWER` — the server never handles raw audio in v1.

## 6. Coding round (`/interviews/{sessionId}/coding`)

| Method | Path | Description |
|---|---|---|
| GET | `/coding/{turnId}/problem` | Problem statement, starter code, visible test cases |
| POST | `/coding/{turnId}/run` | Execute against visible test cases only (sandboxed) |
| POST | `/coding/{turnId}/submit` | Execute against visible + hidden, persist `CodingSubmission`, trigger AI review |
| GET | `/coding/{turnId}/hint` | AI hint (rate-limited, costs a "hint token" server-side) |

## 7. System design round (`/interviews/{sessionId}/system-design`)

| Method | Path | Description |
|---|---|---|
| POST | `/system-design/{turnId}` | Submit diagram (whiteboard JSON or uploaded image) |
| GET | `/system-design/{turnId}/review` | AI architecture review + scalability suggestions |

## 8. Dashboard & analytics (`/analytics`)

| Method | Path | Description |
|---|---|---|
| GET | `/analytics/dashboard` | Widget bundle: streak, avg scores, weekly progress, recent interviews |
| GET | `/analytics/skill-radar` | Per-skill scores for radar chart |
| GET | `/analytics/trends?metric=accuracy\|speaking\|score&range=30d` | Time-series for line/bar charts |
| GET | `/analytics/leaderboard?scope=global\|role` | Ranked XP leaderboard |
| GET | `/analytics/heatmap` | Daily activity heatmap data |

## 9. Learning hub (`/learning`)

| Method | Path | Description |
|---|---|---|
| GET | `/learning/recommendations` | AI-curated resources based on skill gaps |
| POST | `/learning/recommendations/{id}/complete` | Mark resource as completed |
| GET | `/learning/roadmap` | Personal improvement roadmap |

## 10. Profile extras (`/profile`)

| Method | Path | Description |
|---|---|---|
| GET | `/profile/achievements` | Badges + XP level |
| GET | `/profile/resume-history` | Past resume versions + score trend |

## 11. Admin (`/admin`) — requires `ROLE_ADMIN`

| Method | Path | Description |
|---|---|---|
| GET | `/admin/users` | Paginated user list, filter/search |
| PATCH | `/admin/users/{id}/status` | Suspend/reactivate |
| GET | `/admin/analytics/overview` | Platform-wide metrics |
| GET | `/admin/api-usage` | AI cost/usage logs, filterable by provider/date |
| GET | `/admin/feature-flags` / `PATCH /admin/feature-flags/{key}` | Feature flag management |
| GET | `/admin/logs` | System log search |

## 12. AI Career tools (`/ai-tools`)

| Method | Path | Description |
|---|---|---|
| POST | `/ai-tools/career-advice` | Free-form career Q&A grounded in user's profile/history |
| POST | `/ai-tools/resume-builder` | Generate resume draft from structured input |
| POST | `/ai-tools/cover-letter` | Generate cover letter for a target role/company |
| POST | `/ai-tools/linkedin-optimizer` | Suggest LinkedIn headline/summary improvements |

## 13. Swagger / OpenAPI

`springdoc-openapi` mounted at `/swagger-ui.html`, spec at
`/v3/api-docs`. Every controller documented with `@Operation`/`@ApiResponse`
annotations generated alongside the implementation in Phase 2 — this table
is the checklist Phase 2 implements against, not a separate artifact to keep
in sync by hand.
